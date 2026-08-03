package com.with_kim.aloc_study.dto.response;

public record SeoulRealEstateImportResponse(
        int requested,
        int imported,
        int duplicated,
        int cancelled,
        int invalid
) {
}
