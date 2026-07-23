package com.with_kim.aloc_study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission_documents")
@Getter
@NoArgsConstructor
public class SubmissionDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_hint", length = 20)
    private DocHint docHint;

    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SubmissionDocument(
            Submission submission,
            DocHint docHint,
            String s3Bucket,
            String s3Key,
            String originalFilename
    ) {
        this.submission = submission;
        this.docHint = docHint;
        this.s3Bucket = s3Bucket;
        this.s3Key = s3Key;
        this.originalFilename = originalFilename;
        this.createdAt = LocalDateTime.now();
    }

    public enum DocHint {
        BUILDING,
        LAND,
        COLLECTIVE
    }
}
