package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.ReviewRequest;
import com.with_kim.aloc_study.dto.response.ReviewResponse;
import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "현재 로그인한 사용자가 특정 매물에 리뷰를 작성합니다.")
    @PostMapping("/houses/{houseId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable long houseId,
            @RequestBody ReviewRequest request,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        ReviewResponse response = reviewService.createReview(userId, houseId, request);

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "리뷰 삭제", description = "현재 로그인한 사용자가 본인이 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable long reviewId,
            Authentication authentication
    ) {
        long userId = Long.valueOf(authentication.getName());

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 리뷰 상세 정보를 조회합니다.")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable long reviewId
    ) {
        ReviewResponse response = reviewService.getReview(reviewId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 수정", description = "현재 로그인한 사용자가 본인이 작성한 리뷰를 수정합니다.")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview (
            @PathVariable long reviewId,
            @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        long userId = Long.parseLong(authentication.getName());

        ReviewResponse response = reviewService.updateReview(userId, reviewId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 전체 조회", description = "등록된 전체 리뷰 목록을 조회합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> responses = reviewService.getAllReviews();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 매물 리뷰 조회", description = "특정 매물에 등록된 리뷰 목록을 조회합니다.")
    @GetMapping("/houses/{houseId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsByHouse(
            @PathVariable long houseId
    ) {
        List<ReviewResponse> responses = reviewService.getReviewsByHouse(houseId);

        return ResponseEntity.ok(responses);
    }

}
