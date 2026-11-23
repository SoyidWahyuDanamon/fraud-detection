# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY shared/pom.xml shared/
COPY audit-service/pom.xml audit-service/

RUN mvn dependency:go-offline -B -pl audit-service -am

COPY shared/src shared/src
COPY audit-service/src audit-service/src
RUN mvn package -pl audit-service -am -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /app/audit-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", \
  "-XX:+UseVirtualThreads", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]   