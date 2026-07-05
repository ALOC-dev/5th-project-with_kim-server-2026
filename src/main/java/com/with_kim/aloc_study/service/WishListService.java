package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.WishListResponse;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.entity.WishList;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    public WishListResponse addWishList(Long userId, Long houseId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("매물 찾을 수 없습니다."));

        if (wishListRepository.existsByUser_IdAndHouse_Id(userId, houseId)) {
            throw new IllegalArgumentException("이미 찜한 매물입니다.");
        }

        WishList wishList = new WishList(user, house);
        WishList savedWishList = wishListRepository.save(wishList);

        return WishListResponse.from(savedWishList);
    }

    @Transactional(readOnly = true)
    public List<WishListResponse> getMyWishList(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        return wishListRepository.findByUser_Id(userId)
                .stream()
                .map(WishListResponse::from)
                .toList();
    }

    public void deleteWishList(Long userId, Long houseId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        if (!wishListRepository.existsByUser_IdAndHouse_Id(userId, houseId)) {
            throw new ResourceNotFoundException("찜한 매물을 찾을 수 없습니다.");
        }

        wishListRepository.deleteByUser_IdAndHouse_Id(userId, houseId);
    }

}
