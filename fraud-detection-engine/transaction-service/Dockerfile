# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy parent + module POMs first for layer caching
COPY pom.xml .
COPY shared/pom.xml shared/
COPY transaction-service/pom.xml transaction-service/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B -pl transaction-service -am

# Copy source and build
COPY shared/src shared/src
COPY transaction-service/src transaction-service/src
RUN mvn package -pl transaction-service -am -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /app/transaction-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", \
  "-XX:+UseVirtualThreads", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]   