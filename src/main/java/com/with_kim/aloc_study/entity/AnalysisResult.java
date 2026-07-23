package com.with_kim.aloc_study.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analysis_results")
@Getter
@NoArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @Column(name = "current_owner")
    private String currentOwner;

    @Column(name = "analysis_status", nullable = false, length = 30)
    private String analysisStatus = "COMPLETE";

    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    @Column(name = "required_documents_reason", columnDefinition = "TEXT")
    private String requiredDocumentsReason;

    @Column(name = "property_type", length = 30)
    private String propertyType;

    @Column(name = "owner_names", columnDefinition = "TEXT")
    private String ownerNames;

    @Column(name = "owner_matches_contract")
    private Boolean ownerMatchesContract;

    @Column(name = "building_land_owner_match")
    private Boolean buildingLandOwnerMatch;

    @Column(name = "trust_found")
    private Boolean trustFound;

    @Column(name = "mortgage_total")
    private Long mortgageTotal;

    @Column(name = "senior_tenant_deposits_used")
    private Long seniorTenantDepositsUsed;

    @Column(name = "registered_tenant_deposit_total")
    private Long registeredTenantDepositTotal;

    @Column(name = "risk_ratio")
    private Double riskRatio;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "hug_eligible")
    private Boolean hugEligible;

    @Column(name = "lh_eligible")
    private Boolean lhEligible;

    @Column(name = "house_price_used")
    private Long housePriceUsed;

    @Column(name = "house_price_basis", columnDefinition = "TEXT")
    private String housePriceBasis;

    @Column(name = "raw_result_s3_bucket")
    private String rawResultS3Bucket;

    @Column(name = "raw_result_s3_key")
    private String rawResultS3Key;

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisMortgage> mortgageItems = new ArrayList<>();

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "analysisResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisRegistryHit> registryHits = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AnalysisResult(Submission submission) {
        this.submission = submission;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void replaceSummary(
            String analysisStatus,
            String requiredDocuments,
            String requiredDocumentsReason,
            String propertyType,
            String currentOwner,
            String ownerNames,
            Boolean ownerMatchesContract,
            Boolean buildingLandOwnerMatch,
            Boolean trustFound,
            Long mortgageTotal,
            Long seniorTenantDepositsUsed,
            Long registeredTenantDepositTotal,
            Double riskRatio,
            Double riskScore,
            String riskLevel,
            Boolean hugEligible,
            Boolean lhEligible,
            Long housePriceUsed,
            String housePriceBasis,
            String rawResultS3Bucket,
            String rawResultS3Key
    ) {
        this.analysisStatus = analysisStatus == null ? "COMPLETE" : analysisStatus;
        this.requiredDocuments = requiredDocuments;
        this.requiredDocumentsReason = requiredDocumentsReason;
        this.propertyType = propertyType;
        this.currentOwner = currentOwner;
        this.ownerNames = ownerNames;
        this.ownerMatchesContract = ownerMatchesContract;
        this.buildingLandOwnerMatch = buildingLandOwnerMatch;
        this.trustFound = trustFound;
        this.mortgageTotal = mortgageTotal;
        this.seniorTenantDepositsUsed = seniorTenantDepositsUsed;
        this.registeredTenantDepositTotal = registeredTenantDepositTotal;
        this.riskRatio = riskRatio;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.hugEligible = hugEligible;
        this.lhEligible = lhEligible;
        this.housePriceUsed = housePriceUsed;
        this.housePriceBasis = housePriceBasis;
        this.rawResultS3Bucket = rawResultS3Bucket;
        this.rawResultS3Key = rawResultS3Key;
        this.updatedAt = LocalDateTime.now();
    }

    public void clearDetails() {
        documents.clear();
        mortgageItems.clear();
        messages.clear();
        registryHits.clear();
    }

    public void addDocument(
            String docType,
            String inferredPropertyType,
            String currentOwner,
            Integer activeMortgageCount,
            Integer activeEncumbranceCount,
            Boolean hasSeparateLandRegistry,
            Boolean hasDaejigwon,
            String notes
    ) {
        documents.add(new AnalysisDocument(
                this,
                docType,
                inferredPropertyType,
                currentOwner,
                activeMortgageCount,
                activeEncumbranceCount,
                hasSeparateLandRegistry,
                hasDaejigwon,
                notes
        ));
    }

    public void addMortgageItem(Integer rank, String raw, Long amount, Boolean jointCollateral) {
        mortgageItems.add(new AnalysisMortgage(this, rank, raw, amount, jointCollateral));
    }

    public void addMessage(AnalysisMessage.MessageType type, String content, int displayOrder) {
        messages.add(new AnalysisMessage(this, type, content, displayOrder));
    }

    public void addRegistryHit(
            AnalysisRegistryHit.HitType type,
            String keyword,
            Integer rank,
            String line,
            Long amount,
            Boolean cancelled
    ) {
        registryHits.add(new AnalysisRegistryHit(this, type, keyword, rank, line, amount, cancelled));
    }
}
