FROM maven:3.9.6-eclipse-temurin-8 AS builder
WORKDIR /app

COPY . .

RUN mvn -B -DskipTests -Dcheckstyle.skip=true package


FROM eclipse-temurin:8-jre-jammy
WORKDIR /app

COPY --from=builder /app/market-gateway/target/*.jar app.jar

EXPOSE 88
ENTRYPOINT ["java","-jar","app.jar"]
