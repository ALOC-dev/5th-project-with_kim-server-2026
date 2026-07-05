package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.LoginRequest;
import com.with_kim.aloc_study.dto.request.SignUpRequest;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.dto.response.SignUpResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.service.AuthService;
import com.with_kim.aloc_study.service.UserService;
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
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Users user = userService.create(request.getUserId(), request.getPassword(), request.getUsername());

        SignUpResponse response = new SignUpResponse(user.getId(), user.getUserId(), user.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String accessCode, HttpServletRequest httpServletRequest) {
        LoginResponse response = authService.oAuthLogin(accessCode, httpServletRequest);

        return ResponseEntity.ok(response);
    }

//    @RestController
//    @RequestMapping("/api/users")
//    public class UserController {
//
//        @GetMapping("/me")
//        public ResponseEntity<String> me() {
//            return ResponseEntity.ok("인증 성공");
//        }
//    }
}
