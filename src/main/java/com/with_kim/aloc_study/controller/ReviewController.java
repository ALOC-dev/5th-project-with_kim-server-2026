package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.ReviewRequest;
import com.with_kim.aloc_study.dto.response.ReviewResponse;
import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/houses/{houseId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable long houseId,
            @RequestBody ReviewRequest request,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        ReviewResponse response = reviewService.createReview(userId, houseId, request);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable long reviewId,
            Authentication authentication
    ) {
        long userId = Long.valueOf(authentication.getName());

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable long reviewId
    ) {
        ReviewResponse response = reviewService.getReview(reviewId);

        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> responses = reviewService.getAllReviews();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/houses/{houseId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsByHouse(
            @PathVariable long houseId
    ) {
        List<ReviewResponse> responses = reviewService.getReviewsByHouse(houseId);

        return ResponseEntity.ok(responses);
    }

}
