# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY shared/pom.xml shared/
COPY scoring-service/pom.xml scoring-service/

RUN mvn dependency:go-offline -B -pl scoring-service -am

COPY shared/src shared/src
COPY scoring-service/src scoring-service/src
RUN mvn package -pl scoring-service -am -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /app/scoring-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", \
  "-XX:+UseVirtualThreads", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]   