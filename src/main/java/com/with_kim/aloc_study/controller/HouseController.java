package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.service.HouseService;
import com.with_kim.aloc_study.service.InfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final InfrastructureService infrastructureService;

    @GetMapping
    public List<HouseResponse> getHouses(
            @RequestParam(required = false) Double centerLng,
            @RequestParam(required = false) Double centerLat,
            @RequestParam(required = false) Double radius
    ) {
        boolean allPresent = centerLat != null && centerLng != null && radius != null;
        boolean anyPresent = centerLat != null || centerLng != null || radius != null;

        if (anyPresent && !allPresent) {
            throw new InvalidRequestException("범위 검색을 위해서는 centerLat, centerLng, radius를 모두 입력해야 합니다.");
        }

        return houseService.getHousesInRadius(centerLng, centerLat, radius);
    }

    @GetMapping("/{houseId}")
    public HouseResponse getHouse(@PathVariable Long houseId) {
        return houseService.getHouse(houseId);
    }

    @GetMapping("/{houseId}/school-distance")
    public List<HouseSchoolDistanceResponse> getSchoolDistances(@PathVariable Long houseId) {
        return houseService.getSchoolDistances(houseId);
    }

    @GetMapping("/{houseId}/infrastructures")
    public List<InfrastructureResponse> getNearbyInfrastructures(
            @PathVariable Long houseId,
            @RequestParam(required = false) Double radius
    ) {
        return infrastructureService.getNearbyInfrastructures(houseId, radius);
    }
}