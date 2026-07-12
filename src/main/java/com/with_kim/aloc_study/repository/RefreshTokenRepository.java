package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<Refresh, Long> {

    Optional<Refresh> findByToken(String token);

    Optional<Refresh> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
