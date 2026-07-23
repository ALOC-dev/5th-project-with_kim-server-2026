package com.with_kim.aloc_study.repository.projection;

public interface HouseSchoolDistanceProjection {
    Long getHouseId();
    Long getSchoolBuildingId();
    String getSchoolBuildingName();
    Double getDistanceMeters();
}