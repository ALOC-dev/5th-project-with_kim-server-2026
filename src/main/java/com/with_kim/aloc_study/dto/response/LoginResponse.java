package com.with_kim.aloc_study.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private long id;

    private String accessToken;

    private String TokenType;
}
