package com.with_kim.aloc_study.dto.response;

public record HouseSchoolDistanceResponse(
        Long houseId,
        Long schoolBuildingId,
        String schoolBuildingName,
        Double distanceMeters
) {}//학교 건물 - building 매핑 -> 학교 건물 - house 매핑으로 변경