#!/bin/bash

# 数据库迁移脚本
# 用于数据库版本管理和升级

set -e

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

# 数据库配置
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-root123456}
DB_NAME=${DB_NAME:-school_market}

# 迁移脚本目录
MIGRATIONS_DIR="scripts/database/migrations"

# 检查MySQL连接
check_mysql_connection() {
    log_info "Checking MySQL connection..."
    
    if ! command -v mysql &> /dev/null; then
        log_error "MySQL client not found. Please install MySQL client."
        exit 1
    fi
    
    if ! mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD -e "SELECT 1;" &> /dev/null; then
        log_error "Cannot connect to MySQL server. Please check your configuration."
        exit 1
    fi
    
    log_info "MySQL connection successful."
}

# 创建迁移记录表
create_migration_table() {
    log_info "Creating migration table if not exists..."
    
    mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME << EOF
CREATE TABLE IF NOT EXISTS schema_migrations (
    version VARCHAR(255) PRIMARY KEY,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    checksum VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
EOF
}

# 获取已应用的迁移
get_applied_migrations() {
    mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME -sN -e "SELECT version FROM schema_migrations ORDER BY version;" 2>/dev/null || echo ""
}

# 计算文件校验和
calculate_checksum() {
    if command -v md5sum &> /dev/null; then
        md5sum "$1" | cut -d' ' -f1
    elif command -v md5 &> /dev/null; then
        md5 -q "$1"
    else
        log_warn "Cannot calculate checksum (md5sum/md5 not available)"
        echo ""
    fi
}

# 检查迁移是否已应用
is_migration_applied() {
    local version=$1
    local applied_migrations=$(get_applied_migrations)
    
    if echo "$applied_migrations" | grep -q "^${version}$"; then
        return 0
    else
        return 1
    fi
}

# 应用迁移
apply_migration() {
    local migration_file=$1
    local version=$(basename "$migration_file" .sql)
    local checksum=$(calculate_checksum "$migration_file")
    
    log_info "Applying migration: $version"
    
    # 执行迁移SQL
    if mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME < "$migration_file"; then
        # 记录迁移
        mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME << EOF
INSERT INTO schema_migrations (version, checksum) VALUES ('$version', '$checksum');
EOF
        log_info "Migration $version applied successfully."
    else
        log_error "Failed to apply migration: $version"
        exit 1
    fi
}

# 回滚迁移（需要手动实现）
rollback_migration() {
    local version=$1
    log_warn "Rollback functionality not implemented. Please manually revert migration: $version"
}

# 列出迁移状态
list_migrations() {
    log_info "Migration Status:"
    echo "=================="
    
    local applied_migrations=$(get_applied_migrations)
    
    if [ -d "$MIGRATIONS_DIR" ]; then
        for migration_file in "$MIGRATIONS_DIR"/*.sql; do
            if [ -f "$migration_file" ]; then
                local version=$(basename "$migration_file" .sql)
                if is_migration_applied "$version"; then
                    echo "✓ $version (applied)"
                else
                    echo "✗ $version (pending)"
                fi
            fi
        done
    else
        log_warn "Migrations directory not found: $MIGRATIONS_DIR"
    fi
}

# 创建新的迁移文件
create_migration() {
    local description=$1
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local migration_file="$MIGRATIONS_DIR/${timestamp}_${description}.sql"
    
    if [ ! -d "$MIGRATIONS_DIR" ]; then
        mkdir -p "$MIGRATIONS_DIR"
    fi
    
    cat > "$migration_file" << EOF
-- Migration: ${description}
-- Created: $(date)
-- Version: ${timestamp}

-- Add your migration SQL here
-- Example:
-- ALTER TABLE users ADD COLUMN email VARCHAR(255);

EOF
    
    log_info "Created migration file: $migration_file"
    echo "Migration version: $timestamp"
}

# 执行迁移
run_migrations() {
    log_info "Running database migrations..."
    
    if [ ! -d "$MIGRATIONS_DIR" ]; then
        log_warn "Migrations directory not found: $MIGRATIONS_DIR"
        return 0
    fi
    
    # 确保迁移目录存在
    mkdir -p "$MIGRATIONS_DIR"
    
    local pending_count=0
    
    for migration_file in "$MIGRATIONS_DIR"/*.sql; do
        if [ -f "$migration_file" ]; then
            local version=$(basename "$migration_file" .sql)
            
            if ! is_migration_applied "$version"; then
                apply_migration "$migration_file"
                pending_count=$((pending_count + 1))
            fi
        fi
    done
    
    if [ $pending_count -eq 0 ]; then
        log_info "No pending migrations found."
    else
        log_info "Applied $pending_count migration(s)."
    fi
}

# 检查数据库是否已存在表
check_existing_tables() {
    local table_count=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME -sN -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME';" 2>/dev/null || echo "0")
    echo $table_count
}

# 初始化数据库
init_database() {
    log_info "Initializing database..."
    
    # 创建数据库
    mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD << EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF
    
    # 检查是否已有表
    local existing_tables=$(check_existing_tables)
    
    if [ "$existing_tables" -gt 0 ]; then
        log_warn "Database '$DB_NAME' already has $existing_tables table(s)."
        log_warn "Skipping table creation, only adding migration support..."
        
        # 只创建迁移表
        create_migration_table
        
        # 插入初始迁移记录（如果不存在）
        mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME << EOF
INSERT IGNORE INTO schema_migrations (version, description) 
VALUES ('existing_database_setup', '现有数据库环境初始化');
EOF
        
        log_info "Migration support added to existing database."
    else
        log_info "Database is empty, running full initialization..."
        
        # 执行完整初始化脚本
        local init_script="scripts/database/init.sql"
        if [ -f "$init_script" ]; then
            mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME < "$init_script"
            log_info "Database initialization completed."
        else
            log_warn "Init script not found: $init_script"
            # 至少创建迁移表
            create_migration_table
        fi
    fi
}

# 数据库备份
backup_database() {
    local backup_file="backups/${DB_NAME}_backup_$(date +%Y%m%d_%H%M%S).sql"
    
    log_info "Creating database backup: $backup_file"
    
    # 创建备份目录
    mkdir -p backups
    
    # 执行备份
    if command -v mysqldump &> /dev/null; then
        mysqldump -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD --single-transaction --routines --triggers $DB_NAME > "$backup_file"
        
        if [ $? -eq 0 ]; then
            log_info "Database backup completed: $backup_file"
            echo "Backup file: $backup_file"
        else
            log_error "Database backup failed"
            exit 1
        fi
    else
        log_error "mysqldump not found. Cannot create backup."
        exit 1
    fi
}

# 验证迁移
validate_migrations() {
    log_info "Validating migrations..."
    
    local applied_migrations=$(get_applied_migrations)
    local has_errors=false
    
    for migration_file in "$MIGRATIONS_DIR"/*.sql; do
        if [ -f "$migration_file" ]; then
            local version=$(basename "$migration_file" .sql)
            local current_checksum=$(calculate_checksum "$migration_file")
            local stored_checksum=$(mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME -sN -e "SELECT checksum FROM schema_migrations WHERE version='$version';" 2>/dev/null || echo "")
            
            if is_migration_applied "$version"; then
                if [ -n "$stored_checksum" ] && [ "$current_checksum" != "$stored_checksum" ]; then
                    log_error "Migration $version has been modified after application!"
                    has_errors=true
                fi
            fi
        fi
    done
    
    if [ "$has_errors" = false ]; then
        log_info "Migration validation passed."
    else
        log_error "Migration validation failed."
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    cat << EOF
Database Migration Tool

Usage: $0 [COMMAND] [OPTIONS]

Commands:
    init        Initialize database and create migration table (safe for existing databases)
    migrate     Run all pending migrations
    status      Show migration status
    create      Create a new migration file
    validate    Validate applied migrations
    backup      Create database backup before migration
    help        Show this help message

Options:
    --host DB_HOST          Database host (default: localhost)
    --port DB_PORT          Database port (default: 3306)
    --user DB_USER          Database user (default: root)
    --password DB_PASSWORD  Database password (default: root123456)
    --database DB_NAME      Database name (default: school_market)

Examples:
    $0 init                                 # Initialize database
    $0 migrate                              # Run pending migrations
    $0 create add_user_email_field          # Create new migration
    $0 status                               # Show migration status
    $0 --host localhost migrate             # Run migrations with custom host

Environment Variables:
    DB_HOST          Database host
    DB_PORT          Database port
    DB_USER          Database user
    DB_PASSWORD      Database password
    DB_NAME          Database name

EOF
}

# 主函数
main() {
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --host)
                DB_HOST="$2"
                shift 2
                ;;
            --port)
                DB_PORT="$2"
                shift 2
                ;;
            --user)
                DB_USER="$2"
                shift 2
                ;;
            --password)
                DB_PASSWORD="$2"
                shift 2
                ;;
            --database)
                DB_NAME="$2"
                shift 2
                ;;
            init|migrate|status|create|validate|backup|help)
                COMMAND="$1"
                shift
                ;;
            *)
                log_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # 设置默认命令
    COMMAND=${COMMAND:-help}
    
    log_info "Database Migration Tool"
    log_info "Host: $DB_HOST:$DB_PORT, Database: $DB_NAME, User: $DB_USER"
    
    # 检查MySQL连接
    check_mysql_connection
    
    # 执行命令
    case $COMMAND in
        init)
            init_database
            ;;
        migrate)
            run_migrations
            ;;
        status)
            list_migrations
            ;;
        create)
            if [ -z "$1" ]; then
                log_error "Please provide migration description"
                exit 1
            fi
            create_migration "$1"
            ;;
        validate)
            validate_migrations
            ;;
        backup)
            backup_database
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