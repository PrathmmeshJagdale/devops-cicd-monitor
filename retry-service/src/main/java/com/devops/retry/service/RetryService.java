package com.devops.retry.service;

import com.devops.retry.entity.RetryRecord;
import com.devops.retry.repository.RetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final RetryRepository retryRepository;

    @Value("${retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${jenkins.mock-mode:true}")
    private boolean mockMode;

    public RetryRecord triggerRetry(String jobName, Integer buildNumber) {
        long existingRetries = retryRepository
                .countByJobNameAndStatus(jobName, RetryRecord.RetryStatus.TRIGGERED);

        if (existingRetries >= maxAttempts) {
            log.warn("Max retries reached for job: {}", jobName);
            return retryRepository.save(RetryRecord.builder()
                    .jobName(jobName)
                    .originalBuildNumber(buildNumber)
                    .retryAttempt((int) existingRetries + 1)
                    .status(RetryRecord.RetryStatus.MAX_RETRIES_REACHED)
                    .message("Maximum retry attempts (" + maxAttempts + ") reached")
                    .build());
        }

        int attempt = (int) existingRetries + 1;
        log.info("Triggering retry #{} for job: {}", attempt, jobName);

        boolean success = mockMode ? simulateRetry() : callJenkinsApi(jobName);

        RetryRecord record = RetryRecord.builder()
                .jobName(jobName)
                .originalBuildNumber(buildNumber)
                .retryAttempt(attempt)
                .status(success ? RetryRecord.RetryStatus.SUCCESS : RetryRecord.RetryStatus.TRIGGERED)
                .message(success ? "Retry triggered successfully" : "Retry queued for execution")
                .build();

        return retryRepository.save(record);
    }

    private boolean simulateRetry() {
        return new Random().nextBoolean();
    }

    private boolean callJenkinsApi(String jobName) {
        log.info("Would call Jenkins API to retry job: {}", jobName);
        return true;
    }

    public List<RetryRecord> getAllRetries() {
        return retryRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> getRetryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRetries", retryRepository.count());
        stats.put("successfulRetries", retryRepository.countByStatus(RetryRecord.RetryStatus.SUCCESS));
        stats.put("maxRetriesReached", retryRepository.countByStatus(RetryRecord.RetryStatus.MAX_RETRIES_REACHED));
        stats.put("activeRetries", retryRepository.countByStatus(RetryRecord.RetryStatus.TRIGGERED));
        return stats;
    }
}
