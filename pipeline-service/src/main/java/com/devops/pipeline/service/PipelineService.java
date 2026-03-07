package com.devops.pipeline.service;

import com.devops.pipeline.entity.PipelineBuild;
import com.devops.pipeline.repository.PipelineBuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineService {

    private final PipelineBuildRepository buildRepository;
    private final MockJenkinsDataService mockService;
    private final RestTemplate restTemplate;

    @Value("${jenkins.mock-mode:true}")
    private boolean mockMode;

    @Value("${alert.service.url}")
    private String alertServiceUrl;

    @Value("${retry.service.url}")
    private String retryServiceUrl;

    @Scheduled(fixedDelayString = "${scheduler.poll.interval}")
    public void pollJenkins() {
        log.info("Polling Jenkins for build status...");
        List<PipelineBuild> builds = mockMode ?
                mockService.generateMockBuilds() : List.of();

        for (PipelineBuild build : builds) {
            buildRepository.save(build);
            if (build.getStatus() == PipelineBuild.BuildStatus.FAILURE) {
                triggerAlert(build);
                triggerRetry(build);
            }
        }
        log.info("Polling complete. Processed {} builds.", builds.size());
    }

    private void triggerAlert(PipelineBuild build) {
        try {
            Map<String, Object> alertRequest = new HashMap<>();
            alertRequest.put("jobName", build.getJobName());
            alertRequest.put("buildNumber", build.getBuildNumber());
            alertRequest.put("severity", "CRITICAL");
            alertRequest.put("message", "Build #" + build.getBuildNumber() +
                    " failed for job: " + build.getJobName());
            restTemplate.postForEntity(alertServiceUrl + "/api/alerts", alertRequest, Object.class);
            log.info("Alert triggered for failed build: {}", build.getJobName());
        } catch (Exception e) {
            log.warn("Could not trigger alert service: {}", e.getMessage());
        }
    }

    private void triggerRetry(PipelineBuild build) {
        try {
            Map<String, Object> retryRequest = new HashMap<>();
            retryRequest.put("jobName", build.getJobName());
            retryRequest.put("buildNumber", build.getBuildNumber());
            restTemplate.postForEntity(retryServiceUrl + "/api/retry", retryRequest, Object.class);
            log.info("Retry triggered for failed build: {}", build.getJobName());
        } catch (Exception e) {
            log.warn("Could not trigger retry service: {}", e.getMessage());
        }
    }

    public List<PipelineBuild> getRecentBuilds() {
        return buildRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long total = buildRepository.count();
        long success = buildRepository.countByStatus(PipelineBuild.BuildStatus.SUCCESS);
        long failed = buildRepository.countByStatus(PipelineBuild.BuildStatus.FAILURE);
        long running = buildRepository.countByStatus(PipelineBuild.BuildStatus.RUNNING);

        stats.put("totalBuilds", total);
        stats.put("successBuilds", success);
        stats.put("failedBuilds", failed);
        stats.put("runningBuilds", running);
        stats.put("successRate", total > 0 ? Math.round((double) success / total * 100) : 0);
        stats.put("lastUpdated", LocalDateTime.now().toString());
        return stats;
    }

    public List<PipelineBuild> getFailedBuilds() {
        return buildRepository.findByStatus(PipelineBuild.BuildStatus.FAILURE);
    }

    public List<PipelineBuild> getBuildsByJob(String jobName) {
        return buildRepository.findByJobName(jobName);
    }
}
