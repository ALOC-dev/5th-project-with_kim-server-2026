package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.UserUpdateRequest;
import com.with_kim.aloc_study.dto.response.UserResponse;
import com.with_kim.aloc_study.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        UserResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 이름, 학과, 비밀번호를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(
            Authentication authentication,
            @RequestBody UserUpdateRequest request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        UserResponse response = userService.updateMyInfo(userId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        userService.deleteMyAccount(userId);

        return ResponseEntity.noContent().build();
    }
}
