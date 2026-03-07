package com.devops.retry.controller;

import com.devops.retry.entity.RetryRecord;
import com.devops.retry.service.RetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retry")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RetryController {

    private final RetryService retryService;

    @PostMapping
    public ResponseEntity<RetryRecord> triggerRetry(@RequestBody Map<String, Object> request) {
        RetryRecord record = retryService.triggerRetry(
                (String) request.get("jobName"),
                (Integer) request.get("buildNumber")
        );
        return ResponseEntity.ok(record);
    }

    @GetMapping
    public ResponseEntity<List<RetryRecord>> getAllRetries() {
        return ResponseEntity.ok(retryService.getAllRetries());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(retryService.getRetryStats());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "retry-service"));
    }
}
