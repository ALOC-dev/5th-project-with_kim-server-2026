package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.HouseSearchCondition;
import com.with_kim.aloc_study.dto.request.HouseCreateRequest;
import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.service.HouseService;
import com.with_kim.aloc_study.service.InfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "House", description = "매물(House) 조회 및 검색 API")
@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final InfrastructureService infrastructureService;

    @Operation(summary = "매물 등록", description = "인증된 사용자가 매물을 등록합니다.")
    @PostMapping
    public ResponseEntity<HouseResponse> createHouse(
            @RequestBody @Valid HouseCreateRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        HouseResponse response = houseService.createHouse(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "매물 전체 목록 조회", description = "등록된 모든 매물을 페이지 단위로 조회합니다.")
    @GetMapping
    public Page<HouseResponse> getHouses(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return houseService.getAllHouses(pageable);
    }

    @Operation(summary = "매물 단건 조회", description = "매물 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{houseId}")
    public HouseResponse getHouse(
            @Parameter(description = "매물 ID") @PathVariable Long houseId
    ) {
        return houseService.getHouse(houseId);
    }

    @Operation(summary = "매물-학교 거리 조회", description = "특정 매물과 등록된 모든 학교 건물 간의 거리를 반환합니다.")
    @GetMapping("/{houseId}/school-distance")
    public List<HouseSchoolDistanceResponse> getSchoolDistances(
            @Parameter(description = "매물 ID") @PathVariable Long houseId
    ) {
        return houseService.getSchoolDistances(houseId);
    }

    @Operation(summary = "매물 주변 인프라 조회", description = "특정 매물 반경 내 주변 시설(편의점, 지하철 등)을 조회합니다.")
    @GetMapping("/{houseId}/infrastructures")
    public List<InfrastructureResponse> getNearbyInfrastructures(
            @Parameter(description = "매물 ID") @PathVariable Long houseId,
            @Parameter(description = "검색 반경(미터), 기본값 500m") @RequestParam(required = false) Double radius
    ) {
        return infrastructureService.getNearbyInfrastructures(houseId, radius);
    }

    @Operation(
            summary = "매물 조건 검색",
            description = "계약 유형, 가격/보증금/월세, 면적, 위치, 학교 근접도, 주변 시설 등 다양한 조건을 조합해 매물을 검색합니다. " +
                    "모든 조건은 선택 사항이며, 조건이 없으면 전체 매물이 조회됩니다."
    )
    @GetMapping("/search")
    public Page<HouseResponse> searchHouses(
            @Parameter(description = "계약 유형 (SALE: 매매, JEONSE: 전세, MONTHLY: 월세)")
            @RequestParam(required = false) String contractType,

            @Parameter(description = "매매가 최소값 (SALE 전용)") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "매매가 최대값 (SALE 전용)") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "보증금 최소값 (JEONSE, MONTHLY 전용)") @RequestParam(required = false) Long minDeposit,
            @Parameter(description = "보증금 최대값 (JEONSE, MONTHLY 전용)") @RequestParam(required = false) Long maxDeposit,
            @Parameter(description = "월세 최소값 (MONTHLY 전용)") @RequestParam(required = false) Long minMonthlyRent,
            @Parameter(description = "월세 최대값 (MONTHLY 전용)") @RequestParam(required = false) Long maxMonthlyRent,

            @Parameter(description = "면적(㎡) 최소값") @RequestParam(required = false) Double minArea,
            @Parameter(description = "면적(㎡) 최대값") @RequestParam(required = false) Double maxArea,
            @Parameter(description = "최소 방 수") @RequestParam(required = false) Integer minRoomNumber,
            @Parameter(description = "관리비 최대값") @RequestParam(required = false) Long maxManagementFee,
            @Parameter(description = "최소 층수") @RequestParam(required = false) Integer minFloor,
            @Parameter(description = "최대 층수") @RequestParam(required = false) Integer maxFloor,

            @Parameter(description = "원형 검색 중심 위도 (radius와 함께 사용)") @RequestParam(required = false) Double centerLat,
            @Parameter(description = "원형 검색 중심 경도 (radius와 함께 사용)") @RequestParam(required = false) Double centerLng,
            @Parameter(description = "원형 검색 반경(미터)") @RequestParam(required = false) Double radius,
            @Parameter(description = "사각형 검색 남서쪽 위도") @RequestParam(required = false) Double swLat,
            @Parameter(description = "사각형 검색 남서쪽 경도") @RequestParam(required = false) Double swLng,
            @Parameter(description = "사각형 검색 북동쪽 위도") @RequestParam(required = false) Double neLat,
            @Parameter(description = "사각형 검색 북동쪽 경도") @RequestParam(required = false) Double neLng,

            @Parameter(description = "학교 건물 ID (maxDistanceFromSchool과 함께 사용)") @RequestParam(required = false) Long schoolBuildingId,
            @Parameter(description = "학교로부터 최대 거리(미터), schoolBuildingId와 함께 사용") @RequestParam(required = false) Double maxDistanceFromSchool,

            @Parameter(description = "반경 내 최소 마트 개수") @RequestParam(required = false) Integer minMart,
            @Parameter(description = "반경 내 최소 편의점 개수") @RequestParam(required = false) Integer minConvenienceStore,
            @Parameter(description = "반경 내 최소 주차장 개수") @RequestParam(required = false) Integer minParking,
            @Parameter(description = "반경 내 최소 지하철역 개수") @RequestParam(required = false) Integer minSubway,
            @Parameter(description = "반경 내 최소 은행 개수") @RequestParam(required = false) Integer minBank,
            @Parameter(description = "반경 내 최소 우체국 개수") @RequestParam(required = false) Integer minPO,
            @Parameter(description = "반경 내 최소 음식점 개수") @RequestParam(required = false) Integer minRestaurant,
            @Parameter(description = "반경 내 최소 카페 개수") @RequestParam(required = false) Integer minCafe,
            @Parameter(description = "반경 내 최소 병원 개수") @RequestParam(required = false) Integer minHospital,
            @Parameter(description = "반경 내 최소 약국 개수") @RequestParam(required = false) Integer minPharmacy,

            @Parameter(description = "정렬 기준 (PRICE_ASC, PRICE_DESC, AREA_ASC, AREA_DESC, FLOOR_ASC, FLOOR_DESC)")
            @RequestParam(required = false, defaultValue = "PRICE_ASC") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HouseSearchCondition condition = new HouseSearchCondition(
                contractType, minPrice, maxPrice, minDeposit, maxDeposit,
                minMonthlyRent, maxMonthlyRent, minArea, maxArea,
                minRoomNumber, maxManagementFee, minFloor, maxFloor,
                centerLat, centerLng, radius, swLat, swLng, neLat, neLng,
                schoolBuildingId, maxDistanceFromSchool,
                minMart, minConvenienceStore, minParking, minSubway, minBank,
                minPO, minRestaurant, minCafe, minHospital, minPharmacy,
                sort
        );

        Pageable pageable = PageRequest.of(page, size);
        return houseService.searchHouses(condition, pageable);
    }

    @GetMapping("/compare")
    @Operation(summary = "비교 매물 조회", description = "houseId를 통해 최대 3개의 매물의 정보를 조회합니다.")
    public ResponseEntity<List<HouseResponse>> compareHouses(@RequestParam List<Long> houseIds) {
        return ResponseEntity.ok(houseService.compareHouses(houseIds));
    }
}