package com.with_kim.aloc_study.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private int rating;

    private String text;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;
}
