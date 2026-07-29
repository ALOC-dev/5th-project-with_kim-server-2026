package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.AnalysisResult;
import com.with_kim.aloc_study.entity.Submission;

public record SubmissionResponse(
        String submissionId,
        String status,
        String riskLevel,
        Double riskScore,
        AnalysisResultResponse analysis
) {
    public static SubmissionResponse from(Submission submission, AnalysisResult analysis) {
        return new SubmissionResponse(
                submission.getSubmissionId(),
                submission.getStatus().name(),
                submission.getRiskLevel(),
                submission.getRiskScore(),
                analysis == null ? null : AnalysisResultResponse.from(analysis)
        );
    }
}
