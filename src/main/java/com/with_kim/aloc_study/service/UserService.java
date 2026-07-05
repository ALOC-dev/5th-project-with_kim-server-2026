package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.LoginRequest;
import com.with_kim.aloc_study.dto.response.LoginResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.AuthenticationFailedException;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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
        String password = request.getPassword();

        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthenticationFailedException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.generateToken(user);

        return new LoginResponse(user.getId(), token, "Bearer");
    }
}
