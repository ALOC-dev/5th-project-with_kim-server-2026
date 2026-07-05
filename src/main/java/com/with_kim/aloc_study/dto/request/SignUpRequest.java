package com.with_kim.aloc_study.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String userId;

    @NotEmpty(message = "사용자 이름은 필수 항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String confirmPassword;
}
