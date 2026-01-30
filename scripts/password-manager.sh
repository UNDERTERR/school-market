#!/bin/bash

# 密码管理脚本
# 用于生成和管理各环境的密码

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_header() {
    echo -e "${BLUE}[HEADER]${NC} $1"
}

# 生成随机密码
generate_password() {
    local length=${1:-16}
    if command -v openssl &> /dev/null; then
        openssl rand -base64 $length | tr -d "=+/" | cut -c1-$length
    elif command -v pwgen &> /dev/null; then
        pwgen -s $length 1
    else
        # 备用方法：使用 /dev/urandom
        LC_ALL=C tr -dc 'A-Za-z0-9' < /dev/urandom | head -c $length
    fi
}

# 生成强密码
generate_strong_password() {
    local length=${1:-24}
    local chars="A-Za-z0-9!@#$%^&*()_+-="
    LC_ALL=C tr -dc "$chars" < /dev/urandom | head -c $length
}

# 检查环境文件
check_env_file() {
    local env_file=$1
    if [ ! -f "$env_file" ]; then
        log_warn "Environment file $env_file not found. Creating from template..."
        return 1
    fi
    return 0
}

# 创建开发环境配置
create_dev_env() {
    local env_file=".env.dev"
    
    log_header "Creating development environment configuration..."
    
    if check_env_file "$env_file" && [ "$FORCE" != "true" ]; then
        log_warn "Development environment file already exists. Use --force to overwrite."
        return 0
    fi
    
    cat > "$env_file" << EOF
# 开发环境配置 - 无密码，便于开发
# Generated: $(date)

# 数据库配置
MYSQL_ROOT_PASSWORD=
MYSQL_USER=school_market
MYSQL_PASSWORD=
MYSQL_PORT=3306

# Redis配置
REDIS_PORT=6379
REDIS_PASSWORD=

# RabbitMQ配置
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672

# Elasticsearch配置
ELASTICSEARCH_PORT=9200
ELASTIC_PASSWORD=

# Nacos配置
NACOS_PORT=8848
NACOS_AUTH_ENABLE=false

# 应用配置
GATEWAY_PORT=8080
EOF
    
    log_info "Development environment configuration created: $env_file"
}

# 创建测试环境配置
create_test_env() {
    local env_file=".env.test"
    local mysql_pass=$(generate_password 12)
    local redis_pass=$(generate_password 12)
    local rabbit_pass=$(generate_password 12)
    local elastic_pass=$(generate_password 12)
    local nacos_token=$(generate_password 64)
    
    log_header "Creating test environment configuration..."
    
    if check_env_file "$env_file" && [ "$FORCE" != "true" ]; then
        log_warn "Test environment file already exists. Use --force to overwrite."
        return 0
    fi
    
    cat > "$env_file" << EOF
# 测试环境配置 - 自动生成的密码
# Generated: $(date)

# 数据库配置
MYSQL_ROOT_PASSWORD=${mysql_pass}
MYSQL_USER=test_user
MYSQL_PASSWORD=${mysql_pass}
MYSQL_PORT=3307

# Redis配置
REDIS_PORT=6380
REDIS_PASSWORD=${redis_pass}

# RabbitMQ配置
RABBITMQ_USER=test_user
RABBITMQ_PASSWORD=${rabbit_pass}
RABBITMQ_PORT=5673
RABBITMQ_MANAGEMENT_PORT=15673

# Elasticsearch配置
ELASTICSEARCH_PORT=9201
ELASTIC_PASSWORD=${elastic_pass}

# Nacos配置
NACOS_PORT=8849
NACOS_AUTH_ENABLE=true
NACOS_AUTH_TOKEN=${nacos_token}

# 应用配置
GATEWAY_PORT=8081
EOF
    
    log_info "Test environment configuration created: $env_file"
    log_info "Generated passwords have been saved to the file."
}

# 创建生产环境配置
create_prod_env() {
    local env_file=".env.prod"
    local mysql_pass=$(generate_strong_password 32)
    local redis_pass=$(generate_strong_password 24)
    local rabbit_pass=$(generate_strong_password 24)
    local elastic_pass=$(generate_strong_password 24)
    local nacos_token=$(generate_strong_password 64)
    local jwt_secret=$(generate_strong_password 32)
    
    log_header "Creating production environment configuration..."
    
    if check_env_file "$env_file" && [ "$FORCE" != "true" ]; then
        log_warn "Production environment file already exists. Use --force to overwrite."
        return 0
    fi
    
    cat > "$env_file" << EOF
# 生产环境配置 - 强密码（请妥善保管）
# Generated: $(date)
# IMPORTANT: Store these passwords securely!

# 数据库配置
MYSQL_ROOT_PASSWORD=${mysql_pass}
MYSQL_USER=prod_user
MYSQL_PASSWORD=${mysql_pass}
MYSQL_PORT=3306

# Redis配置
REDIS_PORT=6379
REDIS_PASSWORD=${redis_pass}

# RabbitMQ配置
RABBITMQ_USER=prod_user
RABBITMQ_PASSWORD=${rabbit_pass}
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672

# Elasticsearch配置
ELASTICSEARCH_PORT=9200
ELASTIC_PASSWORD=${elastic_pass}

# Nacos配置
NACOS_PORT=8848
NACOS_AUTH_ENABLE=true
NACOS_AUTH_TOKEN=${nacos_token}

# 应用配置
GATEWAY_PORT=8080

# 安全配置
SECRET_KEY=${jwt_secret}
EOF
    
    log_info "Production environment configuration created: $env_file"
    log_warn "IMPORTANT: Please store the production passwords securely!"
    
    # 创建密码备份文件
    local backup_file="passwords_backup_$(date +%Y%m%d_%H%M%S).txt"
    cat > "$backup_file" << EOF
# 密码备份文件 - 请妥善保管！
# Generated: $(date)

=== 生产环境密码 ===

MySQL Root Password: ${mysql_pass}
MySQL User Password: ${mysql_pass}

Redis Password: ${redis_pass}

RabbitMQ Password: ${rabbit_pass}

Elasticsearch Password: ${elastic_pass}

Nacos Auth Token: ${nacos_token}

JWT Secret Key: ${jwt_secret}

=== 连接信息 ===

MySQL: mysql://prod_user:${mysql_pass}@localhost:3306/school_market
Redis: redis://:${redis_pass}@localhost:6379
RabbitMQ: amqp://prod_user:${rabbit_pass}@localhost:5672/
Elasticsearch: http://elastic:${elastic_pass}@localhost:9200

请将此文件存储在安全的地方，并在使用后删除！
EOF
    
    log_info "Password backup created: $backup_file"
    log_warn "Store this backup securely and delete after use!"
}

# 显示密码信息
show_passwords() {
    local env_file=$1
    
    if [ ! -f "$env_file" ]; then
        log_error "Environment file not found: $env_file"
        return 1
    fi
    
    log_header "Password information from $env_file"
    echo "=================================="
    
    # 敏感信息，显示部分
    while IFS= read -r line; do
        if [[ $line =~ ^([A-Z_]+_PASSWORD=)(.+) ]]; then
            key="${BASH_REMATCH[1]}"
            value="${BASH_REMATCH[2]}"
            if [ -n "$value" ]; then
                masked="${value:0:3}***"
                echo "${key}${masked}"
            else
                echo "${key}<empty>"
            fi
        elif [[ $line =~ ^SECRET_KEY=(.+) ]]; then
            value="${BASH_REMATCH[1]}"
            if [ -n "$value" ]; then
                masked="${value:0:3}***"
                echo "SECRET_KEY=${masked}"
            else
                echo "SECRET_KEY=<empty>"
            fi
        elif [[ $line =~ ^NACOS_AUTH_TOKEN=(.+) ]]; then
            value="${BASH_REMATCH[1]}"
            if [ -n "$value" ]; then
                masked="${value:0:3}***"
                echo "NACOS_AUTH_TOKEN=${masked}"
            else
                echo "NACOS_AUTH_TOKEN=<empty>"
            fi
        fi
    done < "$env_file"
    
    echo "=================================="
    log_warn "Passwords are masked for security. Use 'show-full' to see complete values."
}

# 验证配置
validate_config() {
    local env_file=$1
    
    if [ ! -f "$env_file" ]; then
        log_error "Environment file not found: $env_file"
        return 1
    fi
    
    log_header "Validating configuration: $env_file"
    
    local errors=0
    
    # 检查必需变量
    local required_vars=("MYSQL_USER" "MYSQL_PORT" "REDIS_PORT" "ELASTICSEARCH_PORT" "NACOS_PORT" "GATEWAY_PORT")
    
    for var in "${required_vars[@]}"; do
        if ! grep -q "^${var}=" "$env_file"; then
            log_error "Missing required variable: $var"
            errors=$((errors + 1))
        fi
    done
    
    # 检查密码安全性（仅生产环境）
    if [[ "$env_file" == *"prod"* ]]; then
        local empty_passwords=$(grep -c "_PASSWORD=$" "$env_file" || true)
        if [ "$empty_passwords" -gt 0 ]; then
            log_warn "Found $empty_passwords empty passwords in production environment"
        fi
    fi
    
    if [ "$errors" -eq 0 ]; then
        log_info "Configuration validation passed"
        return 0
    else
        log_error "Configuration validation failed with $errors errors"
        return 1
    fi
}

# 显示使用帮助
show_help() {
    cat << EOF
Password Management Tool

Usage: $0 [COMMAND] [OPTIONS]

Commands:
    dev         Create development environment configuration
    test        Create test environment with generated passwords
    prod        Create production environment with strong passwords
    show FILE   Show masked passwords from environment file
    show-full FILE  Show complete passwords (use with caution)
    validate FILE Validate environment configuration
    generate [LENGTH] Generate a random password
    help        Show this help message

Options:
    --force     Force overwrite existing files

Examples:
    $0 dev                    # Create dev environment
    $0 test                   # Create test environment with passwords
    $0 prod                   # Create production environment with strong passwords
    $0 validate .env.prod     # Validate production configuration
    $0 show .env.test         # Show masked passwords
    $0 generate 24            # Generate 24-character password

Environment Files:
    .env.dev      Development (no passwords)
    .env.test     Test (generated passwords)
    .env.prod     Production (strong passwords)

Security Notes:
- Store production passwords securely
- Use different passwords for different environments
- Rotate passwords regularly
- Never commit passwords to version control

EOF
}

# 主函数
main() {
    COMMAND=${1:-help}
    FORCE="false"
    
    # 解析选项
    while [[ $# -gt 0 ]]; do
        case $1 in
            --force)
                FORCE="true"
                shift
                ;;
            dev|test|prod|show|show-full|validate|generate|help)
                if [ "$COMMAND" = "help" ]; then
                    COMMAND="$1"
                fi
                shift
                ;;
            *)
                break
                ;;
        esac
    done
    
    case $COMMAND in
        dev)
            create_dev_env
            ;;
        test)
            create_test_env
            ;;
        prod)
            create_prod_env
            ;;
        show)
            if [ -z "$1" ]; then
                log_error "Please specify environment file"
                exit 1
            fi
            show_passwords "$1"
            ;;
        show-full)
            if [ -z "$1" ]; then
                log_error "Please specify environment file"
                exit 1
            fi
            log_warn "Showing complete passwords - ensure you are in a secure environment!"
            cat "$1"
            ;;
        validate)
            if [ -z "$1" ]; then
                log_error "Please specify environment file"
                exit 1
            fi
            validate_config "$1"
            ;;
        generate)
            local length=${1:-16}
            generate_strong_password "$length"
            ;;
        help)
            show_help
            ;;
        *)
            log_error "Unknown command: $COMMAND"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"