package com.devops.report.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date")
    private LocalDateTime reportDate;

    @Column(name = "total_builds")
    private Long totalBuilds;

    @Column(name = "successful_builds")
    private Long successfulBuilds;

    @Column(name = "failed_builds")
    private Long failedBuilds;

    @Column(name = "success_rate")
    private Double successRate;

    @Column(name = "total_alerts")
    private Long totalAlerts;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "email_sent")
    private boolean emailSent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
