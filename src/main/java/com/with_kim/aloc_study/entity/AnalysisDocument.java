package com.with_kim.aloc_study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analysis_documents")
@Getter
@NoArgsConstructor
public class AnalysisDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @Column(name = "doc_type", length = 20)
    private String docType;

    @Column(name = "inferred_property_type", length = 30)
    private String inferredPropertyType;

    @Column(name = "current_owner")
    private String currentOwner;

    @Column(name = "active_mortgage_count")
    private Integer activeMortgageCount;

    @Column(name = "active_encumbrance_count")
    private Integer activeEncumbranceCount;

    @Column(name = "has_separate_land_registry")
    private Boolean hasSeparateLandRegistry;

    @Column(name = "has_daejigwon")
    private Boolean hasDaejigwon;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public AnalysisDocument(
            AnalysisResult analysisResult,
            String docType,
            String inferredPropertyType,
            String currentOwner,
            Integer activeMortgageCount,
            Integer activeEncumbranceCount,
            Boolean hasSeparateLandRegistry,
            Boolean hasDaejigwon,
            String notes
    ) {
        this.analysisResult = analysisResult;
        this.docType = docType;
        this.inferredPropertyType = inferredPropertyType;
        this.currentOwner = currentOwner;
        this.activeMortgageCount = activeMortgageCount;
        this.activeEncumbranceCount = activeEncumbranceCount;
        this.hasSeparateLandRegistry = hasSeparateLandRegistry;
        this.hasDaejigwon = hasDaejigwon;
        this.notes = notes;
    }
}
