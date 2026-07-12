package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.LoginRequest;
import com.with_kim.aloc_study.dto.request.ReissueRequest;
import com.with_kim.aloc_study.dto.request.SignUpRequest;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.dto.response.SignUpResponse;
import com.with_kim.aloc_study.dto.response.TokenResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.service.AuthService;
import com.with_kim.aloc_study.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    @Operation(summary = "회원가입", description = "로그인 ID, 사용자 이름, 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Users user = userService.create(request.getLoginId(), request.getPassword(), request.getUsername());

        SignUpResponse response = new SignUpResponse(user.getId(), user.getLoginId(), user.getUsername());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "로그인 ID와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카카오 로그인 페이지 이동", description = "카카오 OAuth 인증 페이지로 리다이렉트합니다.")
    @GetMapping("/kakao")
    public ResponseEntity<Void> redirectToKakao() {
        String encodedRedirectUri = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + encodedRedirectUri;

        return ResponseEntity.status(302)
                .header("Location", kakaoAuthUrl)
                .build();
    }

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드로 로그인하고 JWT 토큰을 발급받습니다.")
    @GetMapping("/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String accessCode, HttpServletRequest httpServletRequest) {
        LoginResponse response = authService.oAuthLogin(accessCode, httpServletRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "accessToken 갱신", description = "refreshToken을 사용하여 새로운 accessToken을 발급받습니다.")
    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody ReissueRequest request) {
        return userService.reissue(request.refreshToken());
    }
}
