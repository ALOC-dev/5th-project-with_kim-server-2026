package com.with_kim.aloc_study.repository.projection;

public interface HouseWithDistanceProjection {
    Long getHouseId();
    Long getBuildingId();
    String getAddress();
    Long getPrice();
    Double getArea();
    Double getDistanceMeters();
}
