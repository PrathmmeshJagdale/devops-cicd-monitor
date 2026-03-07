package com.devops.pipeline.controller;

import com.devops.pipeline.entity.PipelineBuild;
import com.devops.pipeline.service.PipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pipelines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PipelineController {

    private final PipelineService pipelineService;

    @GetMapping
    public ResponseEntity<List<PipelineBuild>> getRecentBuilds() {
        return ResponseEntity.ok(pipelineService.getRecentBuilds());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(pipelineService.getDashboardStats());
    }

    @GetMapping("/failed")
    public ResponseEntity<List<PipelineBuild>> getFailedBuilds() {
        return ResponseEntity.ok(pipelineService.getFailedBuilds());
    }

    @GetMapping("/job/{jobName}")
    public ResponseEntity<List<PipelineBuild>> getBuildsByJob(@PathVariable String jobName) {
        return ResponseEntity.ok(pipelineService.getBuildsByJob(jobName));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "pipeline-service"));
    }
}
