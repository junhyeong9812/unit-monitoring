# Unit Monitoring System

Spring Boot 4.0 기반 모니터링 시스템 (Prometheus + Grafana + Loki + Alertmanager)

## 📋 구성 요소

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Spring Boot App | 8080 | 메인 애플리케이션 |
| Prometheus | 9090 | 메트릭 수집 및 저장 |
| Grafana | 3000 | 대시보드 및 시각화 |
| Loki | 3100 | 로그 집계 |
| Alertmanager | 9093 | 알림 관리 |

## 🚀 빠른 시작

### 1. 전체 스택 실행

```bash
docker-compose up -d --build
```

### 2. 서비스 접속

- **Grafana**: http://localhost:3000 (admin / admin123)
- **Prometheus**: http://localhost:9090
- **Alertmanager**: http://localhost:9093
- **Application**: http://localhost:8080

### 3. 종료

```bash
docker-compose down
```

## 📁 프로젝트 구조

```
unit-monitoring/
├── docker/
│   ├── alertmanager/
│   │   └── alertmanager.yml          # Alertmanager 설정
│   ├── grafana/
│   │   └── provisioning/
│   │       ├── dashboards/
│   │       │   ├── dashboards.yml    # 대시보드 프로비저닝
│   │       │   └── spring-boot-dashboard.json
│   │       └── datasources/
│   │           └── datasources.yml   # 데이터소스 설정
│   ├── loki/
│   │   └── loki-config.yml           # Loki 설정
│   └── prometheus/
│       ├── prometheus.yml            # Prometheus 설정
│       └── alert-rules.yml           # 알림 규칙
├── data/                             # 영속화 데이터 (git 제외)
│   ├── alertmanager/
│   ├── grafana/
│   ├── loki/
│   └── prometheus/
├── src/
│   └── main/
│       ├── java/.../
│       │   ├── UnitMonitoringApplication.java
│       │   └── controller/
│       │       ├── SampleController.java
│       │       └── AlertWebhookController.java
│       └── resources/
│           ├── application.yml
│           └── logback-spring.xml
├── Dockerfile
├── docker-compose.yml
├── build.gradle
└── settings.gradle
```

## 🔍 API 엔드포인트

### 애플리케이션

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/hello` | 기본 상태 체크 |
| GET | `/api/slow` | 랜덤 지연 응답 (100ms~2s) |
| GET | `/api/error` | 에러 시뮬레이션 |
| GET | `/api/random` | 랜덤 성공/실패 (80%/20%) |
| GET | `/api/logs` | 다양한 로그 레벨 생성 |
| GET | `/api/metrics-info` | 커스텀 메트릭 조회 |

### Actuator

| URL | 설명 |
|-----|------|
| `/actuator/health` | 헬스 체크 |
| `/actuator/prometheus` | Prometheus 메트릭 |
| `/actuator/metrics` | 메트릭 목록 |
| `/actuator/info` | 애플리케이션 정보 |

## 📊 Grafana 대시보드

### 🎮 이모지 모니터링 대시보드 (권장)

귀여운 이모지로 서버 상태를 한눈에 파악할 수 있는 커스텀 대시보드입니다.

#### Import 방법

1. Grafana 접속: http://localhost:3000
2. 좌측 메뉴 **Dashboards** → **New** → **Import** 클릭
3. `emoji-monitoring-dashboard-v2.json` 파일 업로드
4. **Prometheus** 드롭다운에서 데이터소스 선택
5. **Import** 클릭

#### 대시보드 구성

| 섹션 | 내용 |
|------|------|
| 🏠 서버 친구들 상태 | 앱 서버, Prometheus, Grafana, Loki, Alertmanager 상태 |
| 🧠 메모리 & CPU | 힙/논힙 메모리, 프로세스/시스템 CPU (이모지 상태 + 게이지) |
| 🧵 스레드 & 메모리 추이 | Live/Daemon/Peak 스레드, 힙 메모리 변화 그래프 |
| 🌐 HTTP 요청 파티 | 요청률, 에러 수, 응답속도(p95), 에러율 |
| 🎯 API별 요청 현황 | URI별 요청 통계 |
| 🗑️ GC 청소부 | 가비지 컬렉션 횟수 및 소요 시간 |

#### 상태 이모지 예시
<img width="1902" height="872" alt="image" src="https://github.com/user-attachments/assets/bc177531-20ca-4de5-acef-6b367b4539a6" />
<img width="1899" height="833" alt="image" src="https://github.com/user-attachments/assets/e02195b1-8c47-41b3-90ce-745a6204b04f" />

| 상태 | 낮음 | 높음 |
|------|------|------|
| 메모리 | 🐣 여유로워요~ | 🔥🐓 터질것같아!! |
| CPU | 😴 쉬는 중~ | 🏃‍♂️💨 전력질주!! |
| 서버 | 😊 건강해요! | 😵 죽었어요... |
| 요청 | 😴 조용해요 | 🔥 대박 파티!! |
| 에러율 | 😇 완벽해요! | 😱 위험!! |

### 추가 대시보드 설치

Grafana에서 다음 대시보드 ID로 임포트 가능:
- JVM (Micrometer): `4701`
- Spring Boot Statistics: `6756`

## ⚠️ 알림 규칙

### Application Alerts

| Alert | 조건 | Severity |
|-------|------|----------|
| ApplicationDown | 앱 1분 이상 다운 | Critical |
| HighErrorRate | 에러율 5% 초과 (5분) | Warning |
| SlowResponseTime | p95 응답시간 2초 초과 | Warning |

### JVM Alerts

| Alert | 조건 | Severity |
|-------|------|----------|
| HighMemoryUsage | 힙 메모리 85% 초과 | Warning |
| CriticalMemoryUsage | 힙 메모리 95% 초과 | Critical |
| HighGCTime | GC 시간 500ms 초과 | Warning |

## 🔧 커스터마이징

### 이메일 알림 설정

`docker/alertmanager/alertmanager.yml` 수정:

```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alertmanager@example.com'
  smtp_auth_username: 'your-email@gmail.com'
  smtp_auth_password: 'your-app-password'

receivers:
  - name: 'critical-receiver'
    email_configs:
      - to: 'admin@example.com'
        send_resolved: true
```

### Slack 알림 설정

```yaml
global:
  slack_api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'

receivers:
  - name: 'critical-receiver'
    slack_configs:
      - channel: '#alerts'
        send_resolved: true
```

## 📝 로그 조회 (Loki)

Grafana의 Explore에서 Loki 데이터소스 선택 후:

```
{application="unit-monitoring"}
```

로그 레벨별 필터:
```
{application="unit-monitoring"} |= "ERROR"
{application="unit-monitoring"} |= "WARN"
```

## 🛠️ 개발 환경 실행

로컬에서 Spring Boot만 실행:

```bash
# 모니터링 스택만 실행
docker-compose up -d prometheus grafana loki alertmanager

# Spring Boot 로컬 실행
./gradlew bootRun
```

## 📌 요구사항

- Docker & Docker Compose
- Java 25 (빌드 시)
- Gradle 9.x
