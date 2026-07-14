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
@Table(name = "analysis_mortgages")
@Getter
@NoArgsConstructor
public class AnalysisMortgage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @Column(name = "mortgage_rank")
    private Integer rank;

    @Column(columnDefinition = "TEXT")
    private String raw;

    private Long amount;

    @Column(length = 20)
    private String status;

    @Column(name = "joint_collateral")
    private Boolean jointCollateral;

    public AnalysisMortgage(
            AnalysisResult analysisResult,
            Integer rank,
            String raw,
            Long amount,
            String status,
            Boolean jointCollateral
    ) {
        this.analysisResult = analysisResult;
        this.rank = rank;
        this.raw = raw;
        this.amount = amount;
        this.status = status;
        this.jointCollateral = jointCollateral;
    }
}
