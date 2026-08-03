package com.with_kim.aloc_study.dto.request;

import com.with_kim.aloc_study.dto.MetadataDto;

import java.util.List;

public record HouseUpdateRequest(
        Long buildingId,
        Long price,
        Long deposit,
        Long monthlyRent,
        Double area,
        Integer roomNumber,
        Integer toilet,
        Long managementFee,
        String contractType,
        Integer floor,
        String direction,
        String description,
        MetadataDto metadata,
        List<String> imageUrls
) {
}
