package com.with_kim.aloc_study.dto.request;

public record UserUpdateRequest(
        String username,
        String department,
        Long preferredSchoolBuildingId,
        Long preferredDeposit,
        Long preferredMonthlyRent,
        Long preferredJeonse,
        Long budget,
        Boolean notificationEnabled
) {
}
