package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Infrastructure;

public record InfrastructureResponse(
        Long infrastructureId,
        String category,
        String name,
        String address,
        String mAddress,
        Double latitude,
        Double longitude,
        String externalId
) {
    public static InfrastructureResponse from(Infrastructure infrastructure) {
        return new InfrastructureResponse(
                infrastructure.getId(),
                infrastructure.getCategory().name(),
                infrastructure.getName(),
                infrastructure.getAddress(),
                infrastructure.getMAddress(),
                infrastructure.getLatitude(),
                infrastructure.getLongitude(),
                infrastructure.getExternal_id()
        );
    }
}
