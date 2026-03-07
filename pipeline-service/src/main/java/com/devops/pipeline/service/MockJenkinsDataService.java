package com.devops.pipeline.service;

import com.devops.pipeline.entity.PipelineBuild;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MockJenkinsDataService {

    private final List<String> jobNames = List.of(
            "user-service-pipeline",
            "payment-service-pipeline",
            "order-service-pipeline",
            "notification-service-pipeline",
            "api-gateway-pipeline"
    );

    private final List<String> branches = List.of("main", "develop", "feature/auth", "feature/payment", "hotfix/bug-123");
    private final List<String> authors = List.of("john.doe", "jane.smith", "bob.wilson", "alice.johnson", "charlie.brown");
    private final Random random = new Random();

    public List<PipelineBuild> generateMockBuilds() {
        List<PipelineBuild> builds = new ArrayList<>();
        for (String jobName : jobNames) {
            PipelineBuild.BuildStatus status = getRandomStatus();
            builds.add(PipelineBuild.builder()
                    .jobName(jobName)
                    .buildNumber(random.nextInt(200) + 1)
                    .status(status)
                    .durationMs((long) (random.nextInt(300000) + 30000))
                    .triggeredBy(authors.get(random.nextInt(authors.size())))
                    .branch(branches.get(random.nextInt(branches.size())))
                    .commitHash(generateCommitHash())
                    .jenkinsUrl("http://jenkins:8080/job/" + jobName)
                    .build());
        }
        return builds;
    }

    private PipelineBuild.BuildStatus getRandomStatus() {
        int rand = random.nextInt(10);
        if (rand < 6) return PipelineBuild.BuildStatus.SUCCESS;
        if (rand < 8) return PipelineBuild.BuildStatus.FAILURE;
        if (rand < 9) return PipelineBuild.BuildStatus.RUNNING;
        return PipelineBuild.BuildStatus.ABORTED;
    }

    private String generateCommitHash() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 8);
    }
}
