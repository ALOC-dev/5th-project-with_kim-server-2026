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
@Table(name = "analysis_messages")
@Getter
@NoArgsConstructor
public class AnalysisMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    public AnalysisMessage(AnalysisResult analysisResult, MessageType type, String content, int displayOrder) {
        this.analysisResult = analysisResult;
        this.type = type;
        this.content = content;
        this.displayOrder = displayOrder;
    }

    public enum MessageType {
        FLAG,
        NOTE,
        HUG_REASON,
        LH_REASON
    }
}
