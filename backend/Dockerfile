# ============================================
# STAGE 1: Build with Maven
# ARM64 compatible (Oracle VPS / aarch64)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

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
# Using jammy (Ubuntu) base for ARM64 support
# ============================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user for security (Ubuntu/jammy syntax)
RUN groupadd -g 1001 wms && useradd -m -u 1001 -g wms -s /bin/bash wms

# Install wget for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/*

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

# Environment variable defaults (overridden by compose)
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=dev \
    DB_HOST=db \
    DB_PORT=5432 \
    DB_NAME=wms \
    DB_USER=wms \
    DB_PASS=wms

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
