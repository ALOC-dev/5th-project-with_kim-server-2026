package com.with_kim.aloc_study.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HouseListingStatusUpdateRequest(
        @NotBlank(message = "공개 상태를 입력해야 합니다.")
        String listingStatus
) {
}
