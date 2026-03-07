package com.devops.pipeline.repository;

import com.devops.pipeline.entity.PipelineBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface PipelineBuildRepository extends JpaRepository<PipelineBuild, Long> {
    List<PipelineBuild> findTop50ByOrderByCreatedAtDesc();
    List<PipelineBuild> findByStatus(PipelineBuild.BuildStatus status);
    List<PipelineBuild> findByJobName(String jobName);
    long countByStatus(PipelineBuild.BuildStatus status);

    @Query("SELECT p FROM PipelineBuild p WHERE p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<PipelineBuild> findRecentBuilds(LocalDateTime since);

    @Query("SELECT p.jobName, COUNT(p), SUM(CASE WHEN p.status = 'SUCCESS' THEN 1 ELSE 0 END) " +
           "FROM PipelineBuild p GROUP BY p.jobName")
    List<Object[]> getJobStats();
}
