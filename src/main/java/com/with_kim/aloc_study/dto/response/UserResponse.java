package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Users;

public record UserResponse(
        Long id,
        String loginId,
        String username,
        String department,
        Long preferredSchoolBuildingId,
        Long preferredDeposit,
        Long budget,
        Boolean prefersMonthlyRent,
        Boolean prefersJeonse,
        Boolean notificationEnabled,
        String role
) {
    public static UserResponse from(Users user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getDepartment(),
                user.getPreferredSchoolBuildingId(),
                user.getPreferredDeposit(),
                user.getBudget(),
                user.getPrefersMonthlyRent(),
                user.getPrefersJeonse(),
                user.getNotificationEnabled(),
                user.getRole().name()
        );
    }
}
