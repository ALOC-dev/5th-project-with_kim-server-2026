package com.with_kim.aloc_study.dto;

import com.with_kim.aloc_study.entity.House;

public record HouseSearchFilter(
        House.ContractType contractType,
        Long priceMin,
        Long priceMax,
        Integer roomNumber,
        Boolean excludeBanjiha,
        Integer floorMin,
        Double areaMin,
        Double areaMax,
        House.Direction direction,
        String sggName,
        String emdName,
        String semanticQuery//필터로 커버가 불가능한 것->임베딩으로
) {
    //파싱이 실패할 경우 필터 없이 전부 임베딩으로 진행
    public static HouseSearchFilter fallback(String originalQuery){
        return new HouseSearchFilter(null, null, null, null, null, null,
                null, null, null, null, null, originalQuery);
    }
}
