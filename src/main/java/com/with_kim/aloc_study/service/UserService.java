package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users create(String loginId, String password, String username) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID입니다.");
        }

        Users user = new Users();

        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Users.Role.AGENT);
        user.setUsername(username);
        this.userRepository.save(user);

        return user;
    }
}
