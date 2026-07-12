package com.with_kim.aloc_study.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultDto {

    private boolean success;

    private String message;

    private int code;
}
