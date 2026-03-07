package com.devops.retry.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "retry_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "original_build_number")
    private Integer originalBuildNumber;

    @Column(name = "retry_attempt")
    private Integer retryAttempt;

    @Enumerated(EnumType.STRING)
    private RetryStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum RetryStatus {
        TRIGGERED, SUCCESS, FAILED, MAX_RETRIES_REACHED
    }
}
