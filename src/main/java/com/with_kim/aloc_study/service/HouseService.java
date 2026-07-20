package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.HouseSearchCondition;
import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseQueryRepository;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.projection.HouseSchoolDistanceProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseService {

    private final HouseRepository houseRepository;
    private final MetadataService metadataService;
    private final HouseQueryRepository houseQueryRepository;

    public Page<HouseResponse> getAllHouses(Pageable pageable) {
        return houseRepository.findAllWithBuilding(pageable)
                .map(HouseResponse::from);
    }

    @Transactional(readOnly = true)
    public HouseResponse getHouse(Long houseId) {
        House house = houseRepository.findByIdWithBuilding(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다. id=" + houseId));

        metadataService.updateMetadataIfNeeded(house);

        return HouseResponse.from(house);
    }

    //모든 학교 건물과의 거리 반환
    public List<HouseSchoolDistanceResponse> getSchoolDistances(Long houseId) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다. id=" + houseId));

        List<HouseSchoolDistanceProjection> projections = houseRepository.findAllSchoolDistancesByHouseId(houseId);

        if (projections.isEmpty()) {
            throw new ResourceNotFoundException("등록된 학교 건물이 없습니다.");
        }

        return projections.stream()
                .map(p -> new HouseSchoolDistanceResponse(
                        p.getHouseId(),
                        p.getSchoolBuildingId(),
                        p.getSchoolBuildingName(),
                        Math.round(p.getDistanceMeters() * 100) / 100.0
                ))
                .toList();
    }

    //조건 검색
    public Page<HouseResponse> searchHouses(HouseSearchCondition condition, Pageable pageable) {
        validateSearchCondition(condition);
        return houseQueryRepository.searchHouses(condition, pageable)
                .map(HouseResponse::from);
    }

    private void validateSearchCondition(HouseSearchCondition condition) {
        boolean anyBoundingBox = condition.swLat() != null || condition.swLng() != null
                || condition.neLat() != null || condition.neLng() != null;
        boolean allBoundingBox = condition.swLat() != null && condition.swLng() != null
                && condition.neLat() != null && condition.neLng() != null;
        if (anyBoundingBox && !allBoundingBox) {
            throw new InvalidRequestException("사각형 범위 검색은 swLat, swLng, neLat, neLng가 모두 필요합니다.");
        }

        boolean hasCenterPos = condition.centerLat() != null || condition.centerLng() != null;
        boolean validCenterPos = condition.centerLat() != null && condition.centerLng() != null;
        if(hasCenterPos && !validCenterPos) {
            throw new InvalidRequestException("중심 좌표가 올바르지 않습니다");
        }
        if(validCenterPos && condition.radiusMeters() == null){
            throw new InvalidRequestException("반경을 지정해야 합니다");
        }
        if (condition.radiusMeters() != null && condition.radiusMeters() <= 0) {
            throw new InvalidRequestException("반경은 0보다 커야 합니다.");
        }

        if (allBoundingBox && condition.radiusMeters() != null) {
            throw new InvalidRequestException("사각형 범위와 원형 범위는 동시에 사용할 수 없습니다.");
        }

        if (condition.schoolBuildingId() != null && condition.maxDistanceFromSchool() == null) {
            throw new InvalidRequestException("학교 건물 조건 검색은 maxDistanceFromSchool을 지정해야 합니다.");
        }
        if (condition.maxDistanceFromSchool() != null && condition.schoolBuildingId() == null) {
            throw new InvalidRequestException("maxDistanceFromSchool을 지정하려면 schoolBuildingId가 필요합니다.");
        }
        if (condition.maxDistanceFromSchool() != null && condition.maxDistanceFromSchool() <= 0) {
            throw new InvalidRequestException("학교와의 거리는 0보다 커야 합니다.");
        }
    }
}



