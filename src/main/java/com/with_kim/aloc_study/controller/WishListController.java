package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.WishListResponse;
import com.with_kim.aloc_study.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/{houseId}")
    public ResponseEntity<WishListResponse> addWishList(
            @PathVariable Long houseId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        WishListResponse response = wishListService.addWishList(userId, houseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<WishListResponse>> getMyWishList(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<WishListResponse> response = wishListService.getMyWishList(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<Void> deleteWishList(
            @PathVariable Long houseId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        wishListService.deleteWishList(userId, houseId);
        return ResponseEntity.noContent().build();
    }
}
