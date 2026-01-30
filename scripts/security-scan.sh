#!/bin/bash

# 安全扫描脚本
# 包含依赖漏洞扫描、代码安全检查、容器镜像扫描

set -e

echo "🔒 Starting security scanning..."

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查工具是否安装
check_tool() {
    if ! command -v $1 &> /dev/null; then
        log_warn "$1 is not installed. Skipping $1 scan."
        return 1
    fi
    return 0
}

# 1. 依赖漏洞扫描
dependency_scan() {
    log_info "Starting dependency vulnerability scan..."
    
    if check_tool "mvn"; then
        # 使用OWASP Dependency Check
        if check_tool "dependency-check"; then
            dependency-check --project "school-market" \
                          --scan "./pom.xml" \
                          --format "HTML" \
                          --format "JSON" \
                          --format "XML" \
                          --out "./security-reports/dependency-check" \
                          --failOnCVSS 7 || true
        fi
        
        # 使用Maven插件
        mvn org.owasp:dependency-check-maven:check \
           -Dformat=HTML \
           -Dformat=JSON \
           -DskipTests || true
        
        log_info "Dependency scan completed"
    else
        log_error "Maven not found, skipping dependency scan"
    fi
}

# 2. 代码安全扫描
code_scan() {
    log_info "Starting code security scan..."
    
    if check_tool "spotbugs"; then
        # 使用SpotBugs进行静态代码分析
        mvn spotbugs:check \
           -Dspotbugs.effort=Max \
           -Dspotbugs.threshold=Low || true
        
        log_info "SpotBugs scan completed"
    fi
    
    if check_tool "sonar-scanner"; then
        # 使用SonarQube进行代码质量分析
        if [ ! -z "$SONAR_HOST_URL" ] && [ ! -z "$SONAR_TOKEN" ]; then
            sonar-scanner \
                -Dsonar.projectKey=school-market \
                -Dsonar.sources=. \
                -Dsonar.host.url=$SONAR_HOST_URL \
                -Dsonar.login=$SONAR_TOKEN \
                -Dsonar.exclusions=**/target/**,**/test/** || true
        else
            log_warn "SonarQube credentials not provided, skipping SonarQube scan"
        fi
    fi
}

# 3. 容器镜像扫描
container_scan() {
    log_info "Starting container image security scan..."
    
    IMAGE_NAME=${IMAGE_NAME:-"school-market:latest"}
    
    if check_tool "trivy"; then
        # 使用Trivy扫描容器镜像
        trivy image \
             --format json \
             --output ./security-reports/trivy-report.json \
             --severity HIGH,CRITICAL \
             $IMAGE_NAME || true
        
        trivy image \
             --format table \
             --severity HIGH,CRITICAL \
             $IMAGE_NAME || true
        
        log_info "Trivy scan completed"
    fi
    
    if check_tool "docker"; then
        # 使用Docker Scout扫描
        docker scout cves \
            --format json \
            --output ./security-reports/docker-scout.json \
            $IMAGE_NAME || true
        
        log_info "Docker Scout scan completed"
    fi
}

# 4. SAST扫描 (使用Semgrep)
sast_scan() {
    log_info "Starting SAST scan..."
    
    if check_tool "semgrep"; then
        # 扫描安全漏洞
        semgrep --config=auto \
                --json \
                --output=security-reports/semgrep-report.json \
                . || true
        
        # 生成GitLab SAST报告
        semgrep --config=auto \
                --gitlab-sast \
                --output=gl-sast-report.json \
                . || true
        
        log_info "Semgrep SAST scan completed"
    fi
}

# 5. 密钥泄露扫描
secret_scan() {
    log_info "Starting secret leak scan..."
    
    if check_tool "git-secrets"; then
        # 扫描代码中的敏感信息
        git-secrets --scan -r . || true
        log_info "Git secrets scan completed"
    fi
    
    if check_tool "trufflehog"; then
        # 使用TruffleHog扫描Git历史
        trufflehog filesystem . \
            --json \
            --output security-reports/trufflehog-report.json || true
        
        log_info "TruffleHog scan completed"
    fi
}

# 6. 创建报告目录
setup_reports() {
    log_info "Creating security reports directory..."
    mkdir -p security-reports
}

# 7. 生成综合报告
generate_summary() {
    log_info "Generating security summary report..."
    
    cat > security-reports/summary.md << EOF
# Security Scan Summary

**Scan Date:** $(date)
**Project:** School Market

## Scan Results

### 1. Dependency Vulnerability Scan
- Tool: OWASP Dependency Check
- Report: [dependency-check-report.html](dependency-check-report.html)

### 2. Code Security Scan
- Tool: SpotBugs, SonarQube
- Reports: [SpotBugs Report](../target/spotbugsXml.html)

### 3. Container Image Scan
- Tool: Trivy
- Report: [Trivy Report](trivy-report.json)

### 4. SAST Scan
- Tool: Semgrep
- Report: [Semgrep Report](semgrep-report.json)

### 5. Secret Leak Scan
- Tool: Git-Secrets, TruffleHog
- Report: [TruffleHog Report](trufflehog-report.json)

## Recommendations

1. Review and fix high-severity vulnerabilities immediately
2. Update dependencies to latest secure versions
3. Implement proper input validation
4. Use secure coding practices
5. Regular security scanning and updates

## Next Steps

1. Address identified security issues
2. Implement automated security scanning in CI/CD
3. Conduct regular security assessments
4. Train development team on security best practices
EOF

    log_info "Security summary report generated: security-reports/summary.md"
}

# 主函数
main() {
    setup_reports
    
    # 依赖扫描
    dependency_scan
    
    # 代码扫描
    code_scan
    
    # 容器扫描
    container_scan
    
    # SAST扫描
    sast_scan
    
    # 密钥扫描
    secret_scan
    
    # 生成摘要报告
    generate_summary
    
    log_info "Security scanning completed! Check security-reports/ directory for detailed reports."
}

# 执行主函数
main "$@"