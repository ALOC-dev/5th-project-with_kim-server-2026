package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.LoginRequest;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.dto.response.TokenResponse;
import com.with_kim.aloc_study.entity.Refresh;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.AuthenticationFailedException;
import com.with_kim.aloc_study.repository.RefreshTokenRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public Users create(String loginId, String password, String username) {
        Users user = new Users();

        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        this.userRepository.save(user);

        return user;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String loginId = request.getLoginId();

        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthenticationFailedException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        Refresh savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElse(null);

        if (savedToken == null) {
            refreshTokenRepository.save(
                    Refresh.builder()
                            .userId(user.getId())
                            .token(refreshToken)
                            .expiresAt(LocalDateTime.now().plusDays(7))
                            .build()
            );
        } else {
            savedToken.updateToken(
                    refreshToken,
                    LocalDateTime.now().plusDays(7)
            );
        }

        return new LoginResponse(user.getId(), accessToken, refreshToken, "Bearer");
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token입니다.");
        }

        if (!"refresh".equals(jwtProvider.getTokenType(refreshToken))) {
            throw new IllegalArgumentException("refresh token이 아닙니다.");
        }

        Refresh savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("저장된 refresh token이 아닙니다."));

        if (savedToken.isExpired()) {
            refreshTokenRepository.delete(savedToken);
            throw new IllegalArgumentException("만료된 refresh token입니다.");
        }

        Long userId = jwtProvider.getUserIdAsLong(refreshToken);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        savedToken.updateToken(
                newRefreshToken,
                LocalDateTime.now().plusDays(14)
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
