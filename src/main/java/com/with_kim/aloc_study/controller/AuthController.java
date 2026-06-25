package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.SignUpRequest;
import com.with_kim.aloc_study.dto.response.SignUpResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Users user = userService.create(request.getUsername(), request.getPassword());

        SignUpResponse response = new SignUpResponse(user.getId(), user.getUsername());

        return ResponseEntity.ok(response);
    }
}
