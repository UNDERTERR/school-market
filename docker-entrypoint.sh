#!/bin/bash

# Docker启动脚本

# 设置环境变量
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
export JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx1024m -XX:+UseG1GC"}

# 根据服务设置对应的数据库URL
case "$SERVICE_NAME" in
    "product")
        export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST:-mysql}:${MYSQL_PORT:-3306}/${PMS_DATABASE:-gulimall_pms}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
        ;;
    "order")
        export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST:-mysql}:${MYSQL_PORT:-3306}/${OMS_DATABASE:-gulimall_oms}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
        ;;
    "member")
        export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST:-mysql}:${MYSQL_PORT:-3306}/${UMS_DATABASE:-gulimall_ums}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
        ;;
    "ware")
        export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST:-mysql}:${MYSQL_PORT:-3306}/${WMS_DATABASE:-gulimall_wms}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
        ;;
    "coupon")
        export SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST:-mysql}:${MYSQL_PORT:-3306}/${SMS_DATABASE:-gulimall_sms}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
        ;;
esac

# 等待依赖服务启动
wait_for_service() {
    local host=$1
    local port=$2
    local service_name=$3
    
    echo "Waiting for $service_name to be ready..."
    while ! nc -z $host $port; do
        echo "$service_name is unavailable - sleeping"
        sleep 2
    done
    echo "$service_name is up - continuing"
}

# 安装netcat用于健康检查
if ! command -v nc &> /dev/null; then
    apk add --no-cache netcat-openbsd
fi

# 根据环境变量决定启动哪个服务
case "$SERVICE_NAME" in
    "gateway")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        echo "Starting Gateway Service..."
        exec java $JAVA_OPTS -jar gateway.jar
        ;;
    "product")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Product Service..."
        exec java $JAVA_OPTS -jar product.jar
        ;;
    "order")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Order Service..."
        exec java $JAVA_OPTS -jar order.jar
        ;;
    "cart")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Cart Service..."
        exec java $JAVA_OPTS -jar cart.jar
        ;;
    "ware")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        echo "Starting Ware Service..."
        exec java $JAVA_OPTS -jar ware.jar
        ;;
    "member")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Member Service..."
        exec java $JAVA_OPTS -jar member.jar
        ;;
    "coupon")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        echo "Starting Coupon Service..."
        exec java $JAVA_OPTS -jar coupon.jar
        ;;
    "auth-server")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Auth Server..."
        exec java $JAVA_OPTS -jar auth-server.jar
        ;;
    "third-party")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        echo "Starting Third Party Service..."
        exec java $JAVA_OPTS -jar third-party.jar
        ;;
    "search")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${ELASTICSEARCH_HOST:-elasticsearch} ${ELASTICSEARCH_PORT:-9200} "Elasticsearch"
        echo "Starting Search Service..."
        exec java $JAVA_OPTS -jar search.jar
        ;;
    "seckill")
        wait_for_service ${NACOS_HOST:-nacos} ${NACOS_PORT:-8848} "Nacos"
        wait_for_service ${MYSQL_HOST:-mysql} ${MYSQL_PORT:-3306} "MySQL"
        wait_for_service ${REDIS_HOST:-redis} ${REDIS_PORT:-6379} "Redis"
        echo "Starting Seckill Service..."
        exec java $JAVA_OPTS -jar seckill.jar
        ;;
    *)
        echo "Unknown service: $SERVICE_NAME"
        echo "Available services: gateway, product, order, cart, ware, member, coupon, auth-server, third-party, search, seckill"
        exit 1
        ;;
esac