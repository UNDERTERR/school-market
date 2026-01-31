# 构建阶段 - 使用GitLab Registry或默认镜像
ARG BASE_IMAGE=maven:3.8.1-openjdk-8
FROM ${BASE_IMAGE} AS builder

WORKDIR /app

# 复制pom文件并下载依赖
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

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src
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

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段 - 使用GitLab Registry或默认镜像
ARG RUNTIME_IMAGE=openjdk:8-jre-alpine
FROM ${RUNTIME_IMAGE}

# 安装必要的工具
RUN apk add --no-cache tzdata curl bash

# 设置时区
ENV TZ=Asia/Shanghai

# 创建应用用户
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -u 1001 -G appuser

WORKDIR /app

# 复制jar文件
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

# 设置权限
RUN chown -R appuser:appuser /app

# 切换到应用用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动脚本
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]