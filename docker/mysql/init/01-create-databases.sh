#!/bin/bash
set -e

# 创建所有业务数据库
echo "Creating databases..."

# 连接MySQL并创建所有数据库
mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} << EOF
CREATE DATABASE IF NOT EXISTS gulimall_pms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gulimall_oms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gulimall_ums DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gulimall_wms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS gulimall_sms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 显示所有数据库
SHOW DATABASES;
EOF

echo "All databases created successfully!"

# 导入数据结构（如果SQL文件存在）
if [ -d "/docker-entrypoint-initdb.d/sql" ]; then
    echo "Importing database schemas..."
    
    # 导入商品模块数据库
    if [ -f "/docker-entrypoint-initdb.d/sql/gulimall_pms.sql" ]; then
        mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} gulimall_pms < /docker-entrypoint-initdb.d/sql/gulimall_pms.sql
        echo "Imported gulimall_pms.sql"
    fi
    
    # 导入订单模块数据库
    if [ -f "/docker-entrypoint-initdb.d/sql/gulimall_oms.sql" ]; then
        mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} gulimall_oms < /docker-entrypoint-initdb.d/sql/gulimall_oms.sql
        echo "Imported gulimall_oms.sql"
    fi
    
    # 导入会员模块数据库
    if [ -f "/docker-entrypoint-initdb.d/sql/gulimall_ums.sql" ]; then
        mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} gulimall_ums < /docker-entrypoint-initdb.d/sql/gulimall_ums.sql
        echo "Imported gulimall_ums.sql"
    fi
    
    # 导入仓储模块数据库
    if [ -f "/docker-entrypoint-initdb.d/sql/gulimall_wms.sql" ]; then
        mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} gulimall_wms < /docker-entrypoint-initdb.d/sql/gulimall_wms.sql
        echo "Imported gulimall_wms.sql"
    fi
    
    # 导入营销模块数据库
    if [ -f "/docker-entrypoint-initdb.d/sql/gulimall_sms.sql" ]; then
        mysql -h${MYSQL_HOST:-mysql} -P${MYSQL_PORT:-3306} -u${MYSQL_USER:-root} -p${MYSQL_ROOT_PASSWORD:-} gulimall_sms < /docker-entrypoint-initdb.d/sql/gulimall_sms.sql
        echo "Imported gulimall_sms.sql"
    fi
    
    echo "All database schemas imported successfully!"
fi