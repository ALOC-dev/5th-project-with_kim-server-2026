package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.HouseWithDistanceResponse;
import com.with_kim.aloc_study.dto.response.SchoolBuildingResponse;
import com.with_kim.aloc_study.entity.SchoolBuilding;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.SchoolBuildingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolBuildingService {

    private static final double DEFAULT_RADIUS_METERS = 500;
    private final SchoolBuildingRepository schoolBuildingRepository;
    private final HouseRepository houseRepository;

    public SchoolBuildingResponse getSchoolBuilding(Long schoolBuildingId) {
        SchoolBuilding schoolBuilding = schoolBuildingRepository.findById(schoolBuildingId)
                .orElseThrow(() -> new ResourceNotFoundException("학교 건물을 찾을 수 없습니다. id=" + schoolBuildingId));
        return SchoolBuildingResponse.from(schoolBuilding);
    }

    public List<HouseWithDistanceResponse> getNearbyHouses(Long schoolBuildingId, String sort, Double radiusMeters) {
        schoolBuildingRepository.findById(schoolBuildingId)
                .orElseThrow(() -> new ResourceNotFoundException("학교 건물을 찾을 수 없습니다. id=" + schoolBuildingId));

        double radius = (radiusMeters != null) ? radiusMeters : DEFAULT_RADIUS_METERS;

        if(radius < 0 || radius > 50000){
            throw new InvalidRequestException("반경 지정이 잘못되었습니다.");
        }

        List<HouseWithDistanceResponse> result = houseRepository.findHousesNearSchool(schoolBuildingId, radius).stream()
                .map(p -> new HouseWithDistanceResponse(
                        p.getHouseId(),
                        p.getBuildingId(),
                        p.getAddress(),
                        p.getPrice(),
                        p.getArea(),
                        Math.round(p.getDistanceMeters() * 100) / 100.0
                ))
                .toList();

        // SQL에서 이미 distanceMeters 기준 정렬됨. sort=price 요청 시에만 재정렬
        if ("price".equals(sort)) {
            return result.stream()
                    .sorted(Comparator.comparing(HouseWithDistanceResponse::price))
                    .toList();
        }
        return result;
    }
}
