package com.devops.alert.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "build_number")
    private Integer buildNumber;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(nullable = false)
    private String message;

    @Column(name = "email_sent")
    private boolean emailSent;

    @Column(name = "slack_sent")
    private boolean slackSent;

    @Column(name = "resolved")
    private boolean resolved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Severity {
        WARNING, CRITICAL
    }
}
