package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.House;

public record HouseResponse(
        Long houseId,
        Long buildingId,
        String address,
        Long price,
        Double area,
        Integer roomNumber,
        Integer toilet,
        Long managementFee,
        String contractType,
        Integer floor,
        String direction,
        String description
) {
    public static HouseResponse from(House house) {
        return new HouseResponse(
                house.getId(),
                house.getBuilding().getId(),
                house.getBuilding().getAddress(),
                house.getPrice(),
                house.getArea(),
                house.getRoomNumber(),
                house.getToilet(),
                house.getManagementFee(),
                house.getContractType().name(),
                house.getFloor(),
                house.getDirection().name(),
                house.getDescription()
        );
    }
}