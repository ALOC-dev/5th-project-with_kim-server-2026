package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.LoginRequest;
import com.with_kim.aloc_study.dto.response.KaKaoResponse;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.dto.response.TokenResponse;
import com.with_kim.aloc_study.entity.Refresh;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.AuthenticationFailedException;
import com.with_kim.aloc_study.repository.RefreshTokenRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.security.JwtProvider;
import com.with_kim.aloc_study.util.KakaoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String loginId = request.getLoginId();

        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthenticationFailedException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return issueLoginToken(user);
    }

    @Transactional
    public LoginResponse oAuthLogin(String accessCode, HttpServletRequest httpServletRequest) {
        KaKaoResponse.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        String kakaoAccessToken = oAuthToken.getAccess_token();
        KaKaoResponse.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(kakaoAccessToken);

        String loginId = "kakao_" + kakaoProfile.getId();
        String username = kakaoProfile.getKakao_account().getProfile().getNickname();

        Users user = userRepository.findByLoginId(loginId)
                .orElseGet(() -> {
                    Users newUser = Users.create(
                            loginId,
                            passwordEncoder.encode("KAKAO_USER"),
                            username
                    );

                    return userRepository.save(newUser);
                });

        return issueLoginToken(user);
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

    private LoginResponse issueLoginToken(Users user) {
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
}
