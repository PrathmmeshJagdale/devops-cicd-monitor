package com.devops.alert.service;

import com.devops.alert.entity.Alert;
import com.devops.alert.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final JavaMailSender mailSender;

    @Value("${alert.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${alert.email.recipient:devops@company.com}")
    private String emailRecipient;

    @Value("${alert.slack.enabled:false}")
    private boolean slackEnabled;

    @Value("${alert.slack.webhook-url:}")
    private String slackWebhookUrl;

    public Alert createAlert(String jobName, Integer buildNumber,
                              String severity, String message) {
        Alert alert = Alert.builder()
                .jobName(jobName)
                .buildNumber(buildNumber)
                .severity(Alert.Severity.valueOf(severity))
                .message(message)
                .resolved(false)
                .build();

        boolean emailSent = sendEmail(alert);
        boolean slackSent = sendSlack(alert);
        alert.setEmailSent(emailSent);
        alert.setSlackSent(slackSent);

        Alert saved = alertRepository.save(alert);
        log.info("Alert created: [{}] {} - email:{} slack:{}", severity, jobName, emailSent, slackSent);
        return saved;
    }

    private boolean sendEmail(Alert alert) {
        if (!emailEnabled) {
            log.info("Email alerts disabled. Would send: {}", alert.getMessage());
            return false;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(emailRecipient);
            mail.setSubject("[" + alert.getSeverity() + "] Build Failed: " + alert.getJobName());
            mail.setText("Build #" + alert.getBuildNumber() + " failed.\n\n" +
                    "Job: " + alert.getJobName() + "\n" +
                    "Severity: " + alert.getSeverity() + "\n" +
                    "Message: " + alert.getMessage() + "\n" +
                    "Time: " + alert.getCreatedAt());
            mailSender.send(mail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            return false;
        }
    }

    private boolean sendSlack(Alert alert) {
        if (!slackEnabled || slackWebhookUrl.isEmpty()) {
            log.info("Slack alerts disabled. Would send: {}", alert.getMessage());
            return false;
        }
        try {
            RestTemplate rest = new RestTemplate();
            Map<String, String> payload = new HashMap<>();
            payload.put("text", ":red_circle: *BUILD FAILED*\n*Job:* " + alert.getJobName() +
                    "\n*Build:* #" + alert.getBuildNumber() +
                    "\n*Severity:* " + alert.getSeverity() +
                    "\n*Message:* " + alert.getMessage());
            rest.postForEntity(slackWebhookUrl, payload, String.class);
            return true;
        } catch (Exception e) {
            log.error("Failed to send Slack: {}", e.getMessage());
            return false;
        }
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByResolved(false);
    }

    public Alert resolveAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        alert.setResolved(true);
        return alertRepository.save(alert);
    }

    public Map<String, Object> getAlertStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlerts", alertRepository.count());
        stats.put("activeAlerts", alertRepository.countByResolved(false));
        stats.put("criticalAlerts", alertRepository.countBySeverity(Alert.Severity.CRITICAL));
        stats.put("warningAlerts", alertRepository.countBySeverity(Alert.Severity.WARNING));
        return stats;
    }
}
