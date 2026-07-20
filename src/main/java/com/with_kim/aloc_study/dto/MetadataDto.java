package com.with_kim.aloc_study.dto;

public record MetadataDto(int martCount,
                          int convenienceStoreCount,
                          int parkingCount,
                          int subwayCount,
                          int bankCount,
                          int POCount,
                          int restaurantCount,
                          int cafeCount,
                          int hospitalCount,
                          int pharmacyCount
                          ) {
    public static MetadataDto empty(){
        return new MetadataDto(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
