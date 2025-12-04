package com.example.unit_monitoring.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 샘플 REST Controller
 *
 * 모니터링 테스트를 위한 엔드포인트 제공
 * - 커스텀 메트릭 수집
 * - 로그 생성
 * - 다양한 응답 시뮬레이션
 */
@RestController
@RequestMapping("/api")
public class SampleController {

  private static final Logger log = LoggerFactory.getLogger(SampleController.class);

  private final Counter requestCounter;
  private final Counter errorCounter;
  private final Timer responseTimer;
  private final Random random = new Random();

  public SampleController(MeterRegistry meterRegistry) {
    // 커스텀 메트릭 등록
    this.requestCounter = Counter.builder("app_requests_total")
        .description("Total number of requests")
        .tag("type", "sample")
        .register(meterRegistry);

    this.errorCounter = Counter.builder("app_errors_total")
        .description("Total number of errors")
        .tag("type", "sample")
        .register(meterRegistry);

    this.responseTimer = Timer.builder("app_response_time")
        .description("Response time")
        .tag("endpoint", "sample")
        .register(meterRegistry);
  }

  /**
   * 기본 상태 체크 엔드포인트
   */
  @GetMapping("/hello")
  public ResponseEntity<Map<String, Object>> hello() {
    requestCounter.increment();
    log.info("Hello endpoint called");

    return ResponseEntity.ok(Map.of(
        "message", "Hello from Unit Monitoring!",
        "timestamp", LocalDateTime.now().toString(),
        "status", "OK"
    ));
  }

  /**
   * 랜덤 지연 엔드포인트 (응답 시간 테스트용)
   */
  @GetMapping("/slow")
  public ResponseEntity<Map<String, Object>> slowEndpoint() {
    requestCounter.increment();

    return responseTimer.record(() -> {
      try {
        // 100ms ~ 2000ms 랜덤 지연
        int delay = 100 + random.nextInt(1900);
        log.info("Slow endpoint called, delay: {}ms", delay);
        TimeUnit.MILLISECONDS.sleep(delay);

        return ResponseEntity.ok(Map.of(
            "message", "Slow response completed",
            "delay_ms", delay,
            "timestamp", LocalDateTime.now().toString()
        ));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Slow endpoint interrupted", e);
        errorCounter.increment();
        return ResponseEntity.internalServerError().build();
      }
    });
  }

  /**
   * 에러 시뮬레이션 엔드포인트
   */
  @GetMapping("/error")
  public ResponseEntity<Map<String, Object>> errorEndpoint() {
    requestCounter.increment();
    errorCounter.increment();

    log.error("Error endpoint called - simulating error");

    return ResponseEntity.internalServerError().body(Map.of(
        "error", "Simulated error",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * 랜덤 성공/실패 엔드포인트 (에러율 테스트용)
   */
  @GetMapping("/random")
  public ResponseEntity<Map<String, Object>> randomEndpoint() {
    requestCounter.increment();

    // 20% 확률로 에러 발생
    if (random.nextInt(100) < 20) {
      errorCounter.increment();
      log.warn("Random endpoint - error occurred");
      return ResponseEntity.internalServerError().body(Map.of(
          "error", "Random error occurred",
          "timestamp", LocalDateTime.now().toString()
      ));
    }

    log.info("Random endpoint - success");
    return ResponseEntity.ok(Map.of(
        "message", "Random success",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * 다양한 로그 레벨 테스트
   */
  @GetMapping("/logs")
  public ResponseEntity<Map<String, Object>> logEndpoint() {
    requestCounter.increment();

    log.trace("This is a TRACE log");
    log.debug("This is a DEBUG log");
    log.info("This is an INFO log");
    log.warn("This is a WARN log");
    log.error("This is an ERROR log");

    return ResponseEntity.ok(Map.of(
        "message", "Various log levels generated",
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   * 메트릭 정보 조회
   */
  @GetMapping("/metrics-info")
  public ResponseEntity<Map<String, Object>> metricsInfo() {
    return ResponseEntity.ok(Map.of(
        "total_requests", requestCounter.count(),
        "total_errors", errorCounter.count(),
        "error_rate", requestCounter.count() > 0
            ? (errorCounter.count() / requestCounter.count()) * 100
            : 0,
        "timestamp", LocalDateTime.now().toString()
    ));
  }

  /**
   *
   * */
  @PostMapping("testPost")
  public TestData test2(@RequestBody TestData req) {
    return req;
  }
}