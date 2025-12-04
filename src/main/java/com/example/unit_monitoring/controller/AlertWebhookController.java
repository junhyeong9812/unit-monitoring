package com.example.unit_monitoring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Alertmanager Webhook Controller
 *
 * Alertmanager로부터 알림을 수신하는 웹훅 엔드포인트
 * 실제 운영 환경에서는 Slack, Email, SMS 등으로 알림 전달
 */
@RestController
@RequestMapping("/webhook/alerts")
public class AlertWebhookController {

  private static final Logger log = LoggerFactory.getLogger(AlertWebhookController.class);

  /**
   * 기본 알림 수신 엔드포인트
   */
  @PostMapping
  public ResponseEntity<Map<String, Object>> receiveAlert(@RequestBody Map<String, Object> payload) {
    log.info("===========================================");
    log.info("📬 Alert received at: {}", LocalDateTime.now());
    log.info("Payload: {}", payload);
    log.info("===========================================");

    // 알림 처리 로직 (Slack, Email 등으로 전달)
    processAlert(payload, "DEFAULT");

    return ResponseEntity.ok(Map.of(
        "status", "received",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * Critical 알림 수신 엔드포인트
   */
  @PostMapping("/critical")
  public ResponseEntity<Map<String, Object>> receiveCriticalAlert(@RequestBody Map<String, Object> payload) {
    log.error("===========================================");
    log.error("🚨 CRITICAL Alert received at: {}", LocalDateTime.now());
    log.error("Payload: {}", payload);
    log.error("===========================================");

    // Critical 알림 처리 (즉시 알림)
    processAlert(payload, "CRITICAL");

    return ResponseEntity.ok(Map.of(
        "status", "critical_received",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * Warning 알림 수신 엔드포인트
   */
  @PostMapping("/warning")
  public ResponseEntity<Map<String, Object>> receiveWarningAlert(@RequestBody Map<String, Object> payload) {
    log.warn("===========================================");
    log.warn("⚠️ WARNING Alert received at: {}", LocalDateTime.now());
    log.warn("Payload: {}", payload);
    log.warn("===========================================");

    // Warning 알림 처리
    processAlert(payload, "WARNING");

    return ResponseEntity.ok(Map.of(
        "status", "warning_received",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * 알림 처리 로직
   * 실제 운영 환경에서는 이 메서드에서 외부 서비스로 알림 전달
   */
  private void processAlert(Map<String, Object> payload, String severity) {
    // TODO: 실제 알림 처리 구현
    // - Slack 메시지 전송
    // - Email 발송
    // - SMS 발송
    // - PagerDuty 연동
    // - 데이터베이스 저장

    log.info("Processing {} alert...", severity);

    // 알림 상태 추출
    String status = (String) payload.getOrDefault("status", "unknown");

    if ("resolved".equals(status)) {
      log.info("✅ Alert has been RESOLVED");
    } else {
      log.info("🔴 Alert is FIRING");
    }
  }
}