package com.with_kim.aloc_study.dto;

public record NearbyInfo(
        String nearestCampusName,      //가장 가까운 학교 건물명
        Integer nearestCampusMinutes,  //건물까지 도보 분
        Integer cctvCount              //CCTV 수
) {
    public static NearbyInfo empty() {
        return new NearbyInfo(null, null, null);
    }
}