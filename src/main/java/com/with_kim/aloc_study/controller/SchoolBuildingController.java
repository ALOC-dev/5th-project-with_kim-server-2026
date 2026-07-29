package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.HouseWithDistanceResponse;
import com.with_kim.aloc_study.dto.response.SchoolBuildingResponse;
import com.with_kim.aloc_study.service.SchoolBuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "SchoolBuilding", description = "학교 건물 조회 및 주변 매물 검색 API")
@RestController
@RequiredArgsConstructor
public class SchoolBuildingController {

    private final SchoolBuildingService schoolBuildingService;

    @Operation(summary = "학교 건물 단건 조회", description = "학교 건물 ID로 이름, 건물 번호, 위치 정보를 조회합니다.")
    @GetMapping("/api/school-buildings/{schoolBuildingId}")
    public SchoolBuildingResponse getSchoolBuilding(
            @Parameter(description = "학교 건물 ID") @PathVariable Long schoolBuildingId
    ) {
        return schoolBuildingService.getSchoolBuilding(schoolBuildingId);
    }

    @Operation(
            summary = "학교 건물 주변 매물 조회",
            description = "특정 학교 건물을 기준으로 반경 내 매물을 거리순(기본) 또는 가격순으로 조회합니다."
    )
    @GetMapping("/api/school-buildings/{schoolBuildingId}/houses")
    public List<HouseWithDistanceResponse> getNearbyHouses(
            @Parameter(description = "학교 건물 ID") @PathVariable Long schoolBuildingId,
            @Parameter(description = "정렬 기준 ('price' 입력 시 가격순, 미입력 시 거리순)") @RequestParam(required = false) String sort,
            @Parameter(description = "검색 반경(미터), 기본값 500m, 최대 50000m") @RequestParam(required = false) Double radius
    ) {
        return schoolBuildingService.getNearbyHouses(schoolBuildingId, sort, radius);
    }
}