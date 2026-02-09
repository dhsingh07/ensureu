# Multi-stage build for EnsureU Spring Boot application

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-22 AS build

WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY ensureu-commons/pom.xml ensureu-commons/
COPY ensureu-service/pom.xml ensureu-service/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY ensureu-commons/src ensureu-commons/src
COPY ensureu-service/src ensureu-service/src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:11-jre

WORKDIR /app

# Create non-root user
RUN groupadd -r ensureu && useradd -r -g ensureu ensureu

# Copy JAR from build stage
COPY --from=build /app/ensureu-service/target/ensureu-service-*.jar app.jar

# Create directories for file uploads
RUN mkdir -p /app/uploads/csv /app/uploads/images && \
    chown -R ensureu:ensureu /app

USER ensureu

# Expose application port
EXPOSE 8282

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8282/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Xms256m", "-Xmx1g", "-jar", "app.jar"]
