package com.devops.report.service;

import com.devops.report.entity.Report;
import com.devops.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;

    @Value("${pipeline.service.url}")
    private String pipelineServiceUrl;

    @Value("${alert.service.url}")
    private String alertServiceUrl;

    @Value("${report.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${report.email.recipient:devops@company.com}")
    private String emailRecipient;

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void generateDailyReport() {
        log.info("Generating daily report...");
        generateReport();
    }

    public Report generateReport() {
        Map<String, Object> pipelineStats = fetchPipelineStats();
        Map<String, Object> alertStats = fetchAlertStats();

        long total = toLong(pipelineStats.get("totalBuilds"));
        long success = toLong(pipelineStats.get("successBuilds"));
        long failed = toLong(pipelineStats.get("failedBuilds"));
        long alerts = toLong(alertStats.get("totalAlerts"));
        double rate = total > 0 ? Math.round((double) success / total * 100.0) : 0.0;

        String summary = buildSummary(total, success, failed, rate, alerts);

        Report report = Report.builder()
                .reportDate(LocalDateTime.now())
                .totalBuilds(total)
                .successfulBuilds(success)
                .failedBuilds(failed)
                .successRate(rate)
                .totalAlerts(alerts)
                .summary(summary)
                .emailSent(false)
                .build();

        Report saved = reportRepository.save(report);
        if (emailEnabled) sendReportEmail(saved);
        log.info("Daily report generated. Success rate: {}%", rate);
        return saved;
    }

    private Map<String, Object> fetchPipelineStats() {
        try {
            return restTemplate.getForObject(
                    pipelineServiceUrl + "/api/pipelines/stats", Map.class);
        } catch (Exception e) {
            log.warn("Could not fetch pipeline stats: {}", e.getMessage());
            return Map.of("totalBuilds", 0L, "successBuilds", 0L, "failedBuilds", 0L);
        }
    }

    private Map<String, Object> fetchAlertStats() {
        try {
            return restTemplate.getForObject(
                    alertServiceUrl + "/api/alerts/stats", Map.class);
        } catch (Exception e) {
            log.warn("Could not fetch alert stats: {}", e.getMessage());
            return Map.of("totalAlerts", 0L);
        }
    }

    private String buildSummary(long total, long success, long failed,
                                  double rate, long alerts) {
        return String.format(
                "Daily DevOps Pipeline Report - %s\n\n" +
                "Total Builds: %d\nSuccessful: %d\nFailed: %d\n" +
                "Success Rate: %.1f%%\nTotal Alerts: %d\n\n" +
                "System Status: %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                total, success, failed, rate, alerts,
                rate >= 80 ? "HEALTHY" : rate >= 60 ? "DEGRADED" : "CRITICAL"
        );
    }

    private void sendReportEmail(Report report) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(emailRecipient);
            mail.setSubject("Daily DevOps Report - " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            mail.setText(report.getSummary());
            mailSender.send(mail);
            report.setEmailSent(true);
            reportRepository.save(report);
        } catch (Exception e) {
            log.error("Failed to send report email: {}", e.getMessage());
        }
    }

    private long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.parseLong(obj.toString());
    }

    public List<Report> getAllReports() {
        return reportRepository.findTop30ByOrderByCreatedAtDesc();
    }
}
