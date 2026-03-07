package com.devops.pipeline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pipeline_builds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineBuild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "build_number")
    private Integer buildNumber;

    @Enumerated(EnumType.STRING)
    private BuildStatus status;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "branch")
    private String branch;

    @Column(name = "commit_hash")
    private String commitHash;

    @Column(name = "jenkins_url")
    private String jenkinsUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum BuildStatus {
        SUCCESS, FAILURE, RUNNING, ABORTED, UNKNOWN
    }
}
