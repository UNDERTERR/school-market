# 使用轻量级基础镜像
FROM maven:3.8-eclipse-temurin-8-alpine AS builder

WORKDIR /app

# 1. 先只复制pom文件（利用Docker缓存层）
COPY pom.xml .
COPY market-common/pom.xml ./market-common/
COPY market-product/pom.xml ./market-product/
COPY market-order/pom.xml ./market-order/
COPY market-cart/pom.xml ./market-cart/
COPY market-ware/pom.xml ./market-ware/
COPY market-member/pom.xml ./market-member/
COPY market-coupon/pom.xml ./market-coupon/
COPY market-auth-server/pom.xml ./market-auth-server/
COPY market-gateway/pom.xml ./market-gateway/
COPY market-third-party/pom.xml ./market-third-party/
COPY market-search/pom.xml ./market-search/
COPY market-seckill/pom.xml ./market-seckill/

# 2. 下载依赖（这层会被缓存，只要pom不变就不会重新下载）
RUN mvn dependency:go-offline -B -q

# 3. 复制源代码
COPY market-common/src ./market-common/src
COPY market-product/src ./market-product/src
COPY market-order/src ./market-order/src
COPY market-cart/src ./market-cart/src
COPY market-ware/src ./market-ware/src
COPY market-member/src ./market-member/src
COPY market-coupon/src ./market-coupon/src
COPY market-auth-server/src ./market-auth-server/src
COPY market-gateway/src ./market-gateway/src
COPY market-third-party/src ./market-third-party/src
COPY market-search/src ./market-search/src
COPY market-seckill/src ./market-seckill/src

# 4. 构建（跳过测试，使用并行构建）
RUN mvn clean package -DskipTests -T 4C -q

# 运行阶段 - 使用轻量级JRE
FROM eclipse-temurin:8-jre-alpine

# 安装必要工具
RUN apk add --no-cache tzdata curl bash

ENV TZ=Asia/Shanghai
WORKDIR /app

# 创建非root用户
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -u 1001 -G appuser

# 复制JAR文件
COPY --from=builder /app/market-gateway/target/market-gateway-*.jar ./gateway.jar
COPY --from=builder /app/market-product/target/market-product-*.jar ./product.jar
COPY --from=builder /app/market-order/target/market-order-*.jar ./order.jar
COPY --from=builder /app/market-cart/target/market-cart-*.jar ./cart.jar
COPY --from=builder /app/market-ware/target/market-ware-*.jar ./ware.jar
COPY --from=builder /app/market-member/target/market-member-*.jar ./member.jar
COPY --from=builder /app/market-coupon/target/market-coupon-*.jar ./coupon.jar
COPY --from=builder /app/market-auth-server/target/market-auth-server-*.jar ./auth-server.jar
COPY --from=builder /app/market-third-party/target/market-third-party-*.jar ./third-party.jar
COPY --from=builder /app/market-search/target/market-search-*.jar ./search.jar
COPY --from=builder /app/market-seckill/target/market-seckill-*.jar ./seckill.jar

# 复制启动脚本
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# 设置权限
RUN chown -R appuser:appuser /app

# 切换到非root用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]