package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.HouseSearchCondition;
import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.exception.InvalidRequestException;
import com.with_kim.aloc_study.service.HouseService;
import com.with_kim.aloc_study.service.InfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final InfrastructureService infrastructureService;

    @GetMapping
    public Page<HouseResponse> getHouses(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return houseService.getAllHouses(pageable);
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

    @GetMapping("/search")
    public Page<HouseResponse> searchHouses(
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) Integer minRoomNumber,
            @RequestParam(required = false) Long maxManagementFee,
            @RequestParam(required = false) Integer minFloor,
            @RequestParam(required = false) Integer maxFloor,
            @RequestParam(required = false) Double centerLat,
            @RequestParam(required = false) Double centerLng,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) Double swLat,
            @RequestParam(required = false) Double swLng,
            @RequestParam(required = false) Double neLat,
            @RequestParam(required = false) Double neLng,
            @RequestParam(required = false) Long schoolBuildingId,
            @RequestParam(required = false) Double maxDistanceFromSchool,
            @RequestParam(required = false) Integer minMart,
            @RequestParam(required = false) Integer minConvenienceStore,
            @RequestParam(required = false) Integer minParking,
            @RequestParam(required = false) Integer minSubway,
            @RequestParam(required = false) Integer minBank,
            @RequestParam(required = false) Integer minPO,
            @RequestParam(required = false) Integer minRestaurant,
            @RequestParam(required = false) Integer minCafe,
            @RequestParam(required = false) Integer minHospital,
            @RequestParam(required = false) Integer minPharmacy,
            @RequestParam(required = false, defaultValue = "PRICE_ASC") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HouseSearchCondition condition = new HouseSearchCondition(
                contractType, minPrice, maxPrice, minArea, maxArea,
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
}