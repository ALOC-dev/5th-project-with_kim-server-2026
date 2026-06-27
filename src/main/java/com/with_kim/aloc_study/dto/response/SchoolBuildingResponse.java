package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.SchoolBuilding;

public record SchoolBuildingResponse(
        Long schoolBuildingId,
        String buildingName,
        Integer buildingNumber,
        Double latitude,
        Double longitude
) {
    public static SchoolBuildingResponse from(SchoolBuilding schoolBuilding) {
        return new SchoolBuildingResponse(
                schoolBuilding.getId(),
                schoolBuilding.getBuildingName(),
                schoolBuilding.getBuildingNumber(),
                schoolBuilding.getLatitude(),
                schoolBuilding.getLongitude()
        );
    }
}
