package com.with_kim.aloc_study.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
