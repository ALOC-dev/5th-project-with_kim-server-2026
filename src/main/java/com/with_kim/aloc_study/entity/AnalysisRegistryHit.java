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

@Entity
@Table(name = "analysis_registry_hits")
@Getter
@NoArgsConstructor
public class AnalysisRegistryHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HitType type;

    @Column(length = 50)
    private String keyword;

    @Column(name = "registry_rank")
    private Integer rank;

    @Column(columnDefinition = "TEXT")
    private String line;

    private Boolean cancelled;

    public AnalysisRegistryHit(
            AnalysisResult analysisResult,
            HitType type,
            String keyword,
            Integer rank,
            String line,
            Boolean cancelled
    ) {
        this.analysisResult = analysisResult;
        this.type = type;
        this.keyword = keyword;
        this.rank = rank;
        this.line = line;
        this.cancelled = cancelled;
    }

    public enum HitType {
        ENCUMBRANCE,
        TRUST
    }
}
