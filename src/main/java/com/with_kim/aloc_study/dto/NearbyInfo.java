package com.with_kim.aloc_study.dto;

public record NearbyInfo(
        String nearestCampusName,      //가장 가까운 학교 건물명
        Integer nearestCampusMinutes,  //건물까지 도보 분
        Integer nearestCampusMeters,  //건물까지 거리
        Integer cctvCount,              //CCTV 수
        String nearestSubwayName,  //가장 가까운 지하철역
        Integer nearestSubwayMeters, //가장 가까운 지하철까지 분
        Integer policeCount //반경 내 경찰서 수
) {
    public static NearbyInfo empty() {
        return new NearbyInfo(null, null, null,null, null, null, null);
    }
}