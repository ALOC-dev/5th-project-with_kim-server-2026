package com.with_kim.aloc_study.dto;

public record HouseSearchCondition(
        // 기본 조건
        String contractType,
        Long minPrice, Long maxPrice,
        Long minDeposit, Long maxDeposit,           // 추가
        Long minMonthlyRent, Long maxMonthlyRent,
        Double minArea,
        Double maxArea,
        Integer minRoomNumber,
        Long maxManagementFee,
        Integer minFloor,
        Integer maxFloor,

        // 지도 범위
        Double centerLat,
        Double centerLng,
        Double radiusMeters,

        // 지도 범위 (사각형)
        Double swLat,
        Double swLng,
        Double neLat,
        Double neLng,

        // 학교 건물과의 거리
        Long schoolBuildingId,
        Double maxDistanceFromSchool,

        // 편의시설
        Integer minMart,
        Integer minConvenienceStore,
        Integer minParking,
        Integer minSubway,
        Integer minBank,
        Integer minPO,
        Integer minRestaurant,
        Integer minCafe,
        Integer minHospital,
        Integer minPharmacy,

        // 정렬 순서
        String sort
) {}
