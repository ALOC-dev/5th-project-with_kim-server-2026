package com.with_kim.aloc_study.dto.request;

import com.with_kim.aloc_study.entity.Users;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String loginId;

    @NotEmpty(message = "사용자 이름은 필수 항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String confirmPassword;

    @NotNull(message = "주 건물 ID는 필수 항목입니다.")
    private Long mainBuildingId;

    @NotNull(message = "보조 건물 ID는 필수 항목입니다.")
    private Long subBuildingId;

    @NotNull(message = "최대 보증금은 필수 항목입니다.")
    @PositiveOrZero(message = "최대 보증금은 0 이상이어야 합니다.")
    private Integer maxDeposit;

    @NotNull(message = "주거 유형은 필수 항목입니다.")
    private Users.HousingType housingType;
}
