package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.HouseWithDistanceResponse;
import com.with_kim.aloc_study.dto.response.SchoolBuildingResponse;
import com.with_kim.aloc_study.service.SchoolBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SchoolBuildingController {

    private final SchoolBuildingService schoolBuildingService;

    @GetMapping("/api/school-buildings/{schoolBuildingId}")
    public SchoolBuildingResponse getSchoolBuilding(@PathVariable Long schoolBuildingId) {
        return schoolBuildingService.getSchoolBuilding(schoolBuildingId);
    }

    @GetMapping("/api/school-buildings/{schoolBuildingId}/houses")
    public List<HouseWithDistanceResponse> getNearbyHouses(
            @PathVariable Long schoolBuildingId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double radius
    ) {
        return schoolBuildingService.getNearbyHouses(schoolBuildingId, sort, radius);
    }
}