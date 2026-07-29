package com.with_kim.aloc_study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 사용자가 제출한 등기부등본 분석 요청 정보.
 *
 * submissionId는 SQS 메시지, S3 key, 백엔드 결과 콜백까지 전부 이 값으로
 * 연결되므로(idempotency_key 대체) 애플리케이션에서 생성한 값을 그대로 쓴다.
 * (DB auto-increment PK와는 별개)
 */
@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor // JPA 스펙상 기본 생성자 필요
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false, unique = true, length = 64)
    private String submissionId;

    @Column(nullable = false)
    private String owner;

    @Column(name = "tenant_name", nullable = false)
    private String tenantName;  // 임차인 이름 (분석 로직에는 사용되지 않는 메타데이터)

    private Long deposit;

    private Long price;

    @Column(name = "public_price")
    private Long publicPrice;

    @Column(name = "senior_tenant_deposits")
    private Long seniorTenantDeposits;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 30)
    private PropertyType propertyType;

    // 임대 유형. 현재는 JEONSE(전세)만 분석 지원 — WOLSE(월세)는 접수 자체를 거부하므로
    // DB에는 사실상 JEONSE만 저장되지만, 향후 월세 지원 시를 대비해 컬럼은 만들어둔다.
    @Enumerated(EnumType.STRING)
    @Column(name = "lease_type", nullable = false, length = 10)
    private LeaseType leaseType;

    @Column(name = "s3_bucket", nullable = false)
    private String s3Bucket;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionDocument> documents = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;

    // 목록/필터링처럼 자주 조회할 값은 JSON 안에 묻어두지 않고 별도 컬럼으로 꺼내둔다.
    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Submission(String submissionId, String owner, String tenantName, Long deposit, Long price,
                      Long publicPrice, Long seniorTenantDeposits, LeaseType leaseType,
                      PropertyType propertyType, String s3Bucket, String s3Key) {
        this.submissionId = submissionId;
        this.owner = owner;
        this.tenantName = tenantName;
        this.deposit = deposit;
        this.price = price;
        this.publicPrice = publicPrice;
        this.seniorTenantDeposits = seniorTenantDeposits;
        this.leaseType = leaseType;
        this.propertyType = propertyType;
        this.s3Bucket = s3Bucket;
        this.s3Key = s3Key;
        this.status = SubmissionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public enum LeaseType {
        JEONSE,  // 전세 — 현재 유일하게 분석 지원
        WOLSE    // 월세 — 접수 시 400으로 거부 (향후 지원 예정)
    }

    public enum PropertyType {
        APARTMENT,
        ROW_HOUSE,
        MULTI_FAMILY,
        OFFICETEL,
        SINGLE_FAMILY,
        MULTI_HOUSEHOLD,
        COLLECTIVE
    }

    /**
     * 상태 전이. PENDING(DB 저장 직후) -> QUEUED(SQS 발행 성공)
     * -> ANALYZED(Lambda 결과 수신) -> FAILED(재시도 소진/DLQ)
     */
    public void updateStatus(SubmissionStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void addDocument(SubmissionDocument document) {
        if (this.documents.isEmpty()) {
            this.s3Bucket = document.getS3Bucket();
            this.s3Key = document.getS3Key();
        }
        this.documents.add(document);
        this.updatedAt = LocalDateTime.now();
    }

    /** Lambda로부터 받은 분석 요약을 반영하고 상태를 ANALYZED로 전이한다. */
    public void applyAnalysisSummary(String analysisStatus, String riskLevel, Double riskScore) {
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.analyzedAt = LocalDateTime.now();
        updateStatus("NEEDS_MORE_DOCS".equals(analysisStatus)
                ? SubmissionStatus.NEEDS_MORE_DOCS
                : SubmissionStatus.ANALYZED);
    }

    public enum SubmissionStatus {
        PENDING,
        QUEUED,
        ANALYZED,
        NEEDS_MORE_DOCS,
        FAILED
    }
}
