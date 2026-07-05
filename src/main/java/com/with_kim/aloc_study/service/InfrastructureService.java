package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.entity.Infrastructure;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.InfrastructureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InfrastructureService {

    private final InfrastructureRepository infrastructureRepository;
    private final HouseRepository houseRepository;

    private static final double DEFAULT_RADIUS_METERS = 500.0;

    // 인프라 목록 전체 조회
    public List<InfrastructureResponse> getAllInfrastructures() {
        return infrastructureRepository.findAll().stream()
                .map(InfrastructureResponse::from)
                .toList();
    }

    // 특정 카테고리 인프라 목록 조회
    public List<InfrastructureResponse> getInfrastructuresByCategory(String category) {
        Infrastructure.InfrastructureCategory categoryEnum;
        try {
            categoryEnum = Infrastructure.InfrastructureCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("유효하지 않은 카테고리입니다: " + category);
        }

        return infrastructureRepository.findByCategory(categoryEnum).stream()
                .map(InfrastructureResponse::from)
                .toList();
    }

    // 인프라 명칭 기준 조회
    public List<InfrastructureResponse> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("검색어를 입력해주세요.");
        }

        List<InfrastructureResponse> result = infrastructureRepository.findByNameContaining(name).stream()
                .map(InfrastructureResponse::from)
                .toList();

        return result;
    }

    // 특정 매물 주변 인프라 목록 조회
    public List<InfrastructureResponse> getNearbyInfrastructures(Long houseId, Double radiusMeters) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("매물을 찾을 수 없습니다. id=" + houseId));

        double radius = radiusMeters != null ? radiusMeters : DEFAULT_RADIUS_METERS;

        if (radius <= 0) {
            throw new InvalidRequestException("반경은 0보다 커야 합니다.");
        }

        return infrastructureRepository.findNearbyByHouseId(houseId, radius).stream()
                .map(InfrastructureResponse::from)
                .toList();
    }
}
