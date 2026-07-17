package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Building;
import com.with_kim.aloc_study.entity.House;

public record HouseSearchResponse(
        Long id, String contractType, Long price,Long managementFee,
        Integer roomNumber, Integer floor, Double area, String direction,
        String address,Double latitude, Double longitude, String description
) {
    public static HouseSearchResponse from(House h){
        Building b=h.getBuilding();
        return new HouseSearchResponse(
                h.getId(),
                h.getContractType()!=null ? h.getContractType().name():null,
                h.getPrice(),
                h.getManagementFee(),
                h.getRoomNumber(),
                h.getFloor(),
                h.getArea(),
                h.getDirection()!=null? h.getDirection().name():null,
                b.getAddress(),
                b.getLatitude(),
                b.getLongitude(),
                h.getDescription()
        );
    }
}
