package com.devops.alert.repository;

import com.devops.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findTop50ByOrderByCreatedAtDesc();
    List<Alert> findByResolved(boolean resolved);
    long countBySeverity(Alert.Severity severity);
    long countByResolved(boolean resolved);
}
