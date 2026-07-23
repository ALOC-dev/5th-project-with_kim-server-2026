package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.WishListResponse;
import com.with_kim.aloc_study.service.WishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "WishList", description = "찜 목록 API")
public class WishListController {

    private final WishListService wishListService;

    @Operation(summary = "찜 추가", description = "현재 로그인한 사용자의 찜 목록에 매물을 추가합니다.")
    @PostMapping("/{houseId}")
    public ResponseEntity<WishListResponse> addWishList(
            @PathVariable Long houseId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        WishListResponse response = wishListService.addWishList(userId, houseId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 찜 목록 조회", description = "현재 로그인한 사용자의 찜 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<WishListResponse>> getMyWishList(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<WishListResponse> response = wishListService.getMyWishList(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "찜 삭제", description = "현재 로그인한 사용자의 찜 목록에서 매물을 삭제합니다.")
    @DeleteMapping("/{houseId}")
    public ResponseEntity<Void> deleteWishList(
            @PathVariable Long houseId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        wishListService.deleteWishList(userId, houseId);
        return ResponseEntity.noContent().build();
    }
}
