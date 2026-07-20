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
        Users user = new Users();

        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        this.userRepository.save(user);

        return user;
    }
}
