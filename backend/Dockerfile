# ============================================
# STAGE 1: Build with Maven
# ============================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests -B

# ============================================
# STAGE 2: Runtime with JRE only
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 wms && adduser -u 1001 -G wms -D wms

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R wms:wms /app

USER wms

# Expose port (configurable via SERVER_PORT env)
EXPOSE 8080

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variable defaults
ENV SERVER_PORT=8080 \
    DB_HOST=db \
    DB_PORT=5432 \
    DB_NAME=wms \
    DB_USER=wms \
    DB_PASS=wms

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
