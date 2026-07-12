package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.entity.Users;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private long id;

    private long userId;

    private String username;

    private long houseId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int rating;

    private String text;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;

    public static ReviewResponse from(Review review) {

        Users user = review.getUser();
        House house = review.getHouse();

        return new ReviewResponse(
                review.getId(),
                user.getId(),
                user.getUsername(),
                house.getId(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getRating(),
                review.getText(),
                review.getImage_url1(),
                review.getImage_url2(),
                review.getImage_url3()
        );
    }
}
