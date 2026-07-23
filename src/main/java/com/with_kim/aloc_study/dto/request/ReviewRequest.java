package com.with_kim.aloc_study.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private int cleanlinessRating;

    private int managementRating;

    private int locationRating;

    private int priceRating;

    private String text;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;
}
