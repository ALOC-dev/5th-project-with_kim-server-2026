package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Submission;
import tools.jackson.databind.JsonNode;

public record SubmissionResponse(
        String submissionId,
        String status,
        String riskLevel,
        Double riskScore,
        JsonNode analysis
) {
    public static SubmissionResponse from(Submission submission, JsonNode analysis) {
        return new SubmissionResponse(
                submission.getSubmissionId(),
                submission.getStatus().name(),
                submission.getRiskLevel(),
                submission.getRiskScore(),
                analysis
        );
    }
}