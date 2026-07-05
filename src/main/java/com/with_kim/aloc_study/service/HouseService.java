package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.projection.HouseSchoolDistanceProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseService {

    private final HouseRepository houseRepository;

    public List<HouseResponse> getAllHouses() {
        return houseRepository.findAllWithBuilding().stream()
                .map(HouseResponse::from)
                .toList();
    }

    /*public List<HouseResponse> getHousesInBoundingBox(Double swLat, Double swLng, Double neLat, Double neLng) {
        return houseRepository.findAllInBoundingBox(swLat, swLng, neLat, neLng).stream()
                .map(HouseResponse::from)
                .toList();
    }*/

    public List<HouseResponse> getHousesInRadius(Double centerLng, Double centerLat, Double radius) {
        return houseRepository.findAllWithinRadius(centerLng, centerLat, radius).stream()
                .map(HouseResponse::from)
                .toList();
    }

    public HouseResponse getHouse(Long houseId) {
        House house = houseRepository.findByIdWithBuilding(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다. id=" + houseId));
        return HouseResponse.from(house);
    }

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
}