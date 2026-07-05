package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.WishList;

public record WishListResponse(
        Long wishListId,
        Long houseId,
        Long buildingId,
        String address,
        Long price,
        Double area) {

    public static WishListResponse from(WishList wishList) {

        House house = wishList.getHouse();

        return new WishListResponse(
                wishList.getId(),
                house.getId(),
                house.getBuilding().getId(),
                house.getBuilding().getAddress(),
                house.getPrice(),
                house.getArea()
        );
    }
}
