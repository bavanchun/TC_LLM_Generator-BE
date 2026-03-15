# syntax=docker/dockerfile:1.4
# ============================================
# OPTIMIZED Multi-stage build for Spring Boot
# Uses BuildKit cache mounts for FAST rebuilds
# ============================================

# Stage 1: Build stage with cached Maven dependencies
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies with BuildKit cache mount
# This caches ~/.m2 between builds - HUGE speed improvement
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B -q

# Copy source code
COPY src ./src

# Build with cached .m2 and parallel threads
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B -q -T 1C

# Stage 2: Extract layered JAR for better Docker layer caching
FROM eclipse-temurin:21-jre-alpine AS layers

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="TC_LLM_Generator Team"

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy layers in order of change frequency (least → most)
# dependencies and spring-boot-loader rarely change → cached most of the time
COPY --from=layers --chown=spring:spring /app/dependencies/ ./
COPY --from=layers --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=layers --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=layers --chown=spring:spring /app/application/ ./

USER spring:spring

EXPOSE 8080

# Optimized JVM options for container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
