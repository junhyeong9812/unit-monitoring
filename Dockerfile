# ===========================================
# Stage 1: Build Stage
# ===========================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Gradle Wrapper 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 캐싱을 위해 먼저 다운로드
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# ===========================================
# Stage 2: Runtime Stage
# ===========================================
FROM eclipse-temurin:25-jre

LABEL maintainer="your-email@example.com"
LABEL description="Unit Monitoring Spring Boot Application"

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/unit-monitoring.jar app.jar

# 애플리케이션 포트
EXPOSE 8080

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM 옵션 및 애플리케이션 실행
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]