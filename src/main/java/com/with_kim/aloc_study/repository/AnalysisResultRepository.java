package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    Optional<AnalysisResult> findBySubmission_SubmissionId(String submissionId);
}
