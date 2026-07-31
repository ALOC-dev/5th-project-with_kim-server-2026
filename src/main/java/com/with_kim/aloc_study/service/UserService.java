package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.UserUpdateRequest;
import com.with_kim.aloc_study.dto.response.UserResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        Users user = findUser(userId);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMyInfo(Long userId, UserUpdateRequest request) {
        Users user = findUser(userId);

        if (hasText(request.username())) {
            user.setUsername(request.username());
        }

        if (hasText(request.department())) {
            user.setDepartment(request.department());
        }

        if (request.preferredSchoolBuildingId() != null) {
            user.setPreferredSchoolBuildingId(request.preferredSchoolBuildingId());
        }

        if (request.preferredDeposit() != null) {
            user.setPreferredDeposit(request.preferredDeposit());
        }

        if (request.budget() != null) {
            user.setBudget(request.budget());
        }

        if (request.prefersMonthlyRent() != null) {
            user.setPrefersMonthlyRent(request.prefersMonthlyRent());
        }

        if (request.prefersJeonse() != null) {
            user.setPrefersJeonse(request.prefersJeonse());
        }

        if (request.notificationEnabled() != null) {
            user.setNotificationEnabled(request.notificationEnabled());
        }

        if (hasText(request.newPassword()) || hasText(request.confirmNewPassword())) {
            if (!hasText(request.newPassword()) || !request.newPassword().equals(request.confirmNewPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        return UserResponse.from(user);
    }

    private Users findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. id=" + userId));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
