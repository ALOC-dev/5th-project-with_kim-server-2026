package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.SchoolBuilding;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.SchoolBuildingRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolBuildingRepository schoolBuildingRepository;

    public Users create(String loginId,
                        String password,
                        String username,
                        Long mainBuildingId,
                        Long subBuildingId,
                        Integer maxDeposit,
                        Users.HousingType housingType) {

        SchoolBuilding mainBuilding = schoolBuildingRepository.findById(mainBuildingId)
                .orElseThrow(() -> new ResourceNotFoundException("주 학교 건물을 찾을 수 없습니다."));

        SchoolBuilding subBuilding = schoolBuildingRepository.findById(subBuildingId)
                .orElseThrow(() -> new ResourceNotFoundException("부 학교 건물을 찾을 수 없습니다."));

        Users user = Users.create(
                loginId,
                passwordEncoder.encode(password),
                username,
                mainBuilding,
                subBuilding,
                maxDeposit,
                housingType
        );

        return userRepository.save(user);
    }
}
