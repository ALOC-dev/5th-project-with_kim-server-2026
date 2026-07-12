package com.with_kim.aloc_study.dto.response;

public record HouseWithDistanceResponse(
        Long houseId,
        Long buildingId,
        String address,
        Long price,
        Double area,
        Double distanceMeters
) {} ///api/school-buildings/{schoolBuildingId}/houses 엔드포인트에 필요
