package com.devops.retry.repository;

import com.devops.retry.entity.RetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RetryRepository extends JpaRepository<RetryRecord, Long> {
    List<RetryRecord> findTop50ByOrderByCreatedAtDesc();
    long countByJobNameAndStatus(String jobName, RetryRecord.RetryStatus status);
    long countByStatus(RetryRecord.RetryStatus status);
}
