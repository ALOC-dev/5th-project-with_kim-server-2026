package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findBySubmissionId(String submissionId);
}