package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.HouseResponse;
import com.with_kim.aloc_study.dto.response.HouseSchoolDistanceResponse;
import com.with_kim.aloc_study.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @GetMapping
    public List<HouseResponse> getHouses(
            @RequestParam(required = false) Double centerLng,
            @RequestParam(required = false) Double centerLat,
            @RequestParam(required = false) Double radius
    ) {
        boolean hasRadius = centerLat != null && centerLng != null && radius != null;
        return hasRadius
                ? houseService.getHousesInRadius(centerLng, centerLat, radius)
                : houseService.getAllHouses();
    }

    @GetMapping("/{houseId}")
    public HouseResponse getHouse(@PathVariable Long houseId) {
        return houseService.getHouse(houseId);
    }

    @GetMapping("/{houseId}/school-distance")
    public List<HouseSchoolDistanceResponse> getSchoolDistances(@PathVariable Long houseId) {
        return houseService.getSchoolDistances(houseId);
    }
}