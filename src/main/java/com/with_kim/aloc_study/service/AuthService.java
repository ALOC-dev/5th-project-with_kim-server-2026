package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.KaKaoResponse;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.entity.Refresh;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.repository.RefreshTokenRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.security.JwtProvider;
import com.with_kim.aloc_study.util.KakaoUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    public LoginResponse oAuthLogin(String accessCode, HttpServletRequest httpServletRequest) {
        KaKaoResponse.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        String kakaoAccessToken = oAuthToken.getAccess_token();
        KaKaoResponse.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(kakaoAccessToken);

        String loginId = "kakao_" + kakaoProfile.getId();
        String username = kakaoProfile.getKakao_account().getProfile().getNickname();

        Users user = userRepository.findByLoginId(loginId)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setLoginId(loginId);
                    newUser.setPassword(passwordEncoder.encode("KAKAO_USER"));
                    newUser.setUsername(username);

                    return userRepository.save(newUser);
                });

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
