package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Users;

public record UserResponse(
        Long id,
        String loginId,
        String username,
        String department,
        Long preferredSchoolBuildingId,
        Long preferredDeposit,
        Long preferredMonthlyRent,
        Long preferredJeonse,
        Long budget,
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
                user.getPreferredMonthlyRent(),
                user.getPreferredJeonse(),
                user.getBudget(),
                user.getNotificationEnabled(),
                user.getRole().name()
        );
    }
}
