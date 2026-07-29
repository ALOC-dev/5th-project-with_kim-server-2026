package com.with_kim.aloc_study.dto.request;


import tools.jackson.databind.JsonNode;

public record AnalysisResultCallbackRequest(
        String submissionId,
        JsonNode analysis
) {}
