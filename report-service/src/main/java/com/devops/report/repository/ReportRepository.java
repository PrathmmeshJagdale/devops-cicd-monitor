package com.devops.report.repository;

import com.devops.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findTop30ByOrderByCreatedAtDesc();
}
