package com.example.unit_monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Unit Monitoring Application
 *
 * Spring Boot 4.0 기반 모니터링 데모 애플리케이션
 * - Prometheus 메트릭 수집
 * - Grafana 시각화
 * - Loki 로그 집계
 * - Alertmanager 알림
 */
@SpringBootApplication
public class UnitMonitoringApplication {

  public static void main(String[] args) {
    SpringApplication.run(UnitMonitoringApplication.class, args);
  }
}