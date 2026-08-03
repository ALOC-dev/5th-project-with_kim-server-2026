package com.with_kim.aloc_study.dto.request;

public record UserUpdateRequest(
        String username,
        String department,
        Long preferredSchoolBuildingId,
        Long preferredDeposit,
        Long budget,
        Boolean prefersMonthlyRent,
        Boolean prefersJeonse,
        Boolean notificationEnabled,
        String newPassword,
        String confirmNewPassword
) {
}
