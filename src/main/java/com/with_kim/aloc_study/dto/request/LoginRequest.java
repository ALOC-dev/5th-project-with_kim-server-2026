package com.with_kim.aloc_study.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String userId;

    private String password;
}
