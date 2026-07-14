package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.ReviewRequest;
import com.with_kim.aloc_study.dto.response.ReviewResponse;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.ReviewRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    public ReviewResponse createReview(long userId, long houseId, ReviewRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        if (reviewRepository.existsByUser_IdAndHouse_Id(userId, houseId))
            throw new IllegalArgumentException("이미 리뷰를 작성한 매물입니다.");

        Review review = new Review(
                user,
                house,
                request.getRating(),
                request.getText(),
                request.getImageUrl1(),
                request.getImageUrl2(),
                request.getImageUrl3()
        );

        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }

    // 특정 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewResponse getReview(long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다."));

        return ReviewResponse.from(review);
    }

    // 리뷰 전체 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    // 특정 매물 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByHouse(long houseId) {

        return reviewRepository.findByHouse_Id(houseId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    // 리뷰 수정
    public ReviewResponse updateReview(long userId, long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndUser_Id(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다."));

        review.update(
                request.getRating(),
                request.getText(),
                request.getImageUrl1(),
                request.getImageUrl2(),
                request.getImageUrl3()
        );

        return ReviewResponse.from(review);
    }

    // 리뷰 삭제
    public void deleteReview(long userId, long reviewId) {
        Review review = reviewRepository.findByIdAndUser_Id(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰를 찾을 수 없습니다."));

        reviewRepository.delete(review);
    }
}
