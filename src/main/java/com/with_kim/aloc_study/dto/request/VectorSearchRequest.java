package com.with_kim.aloc_study.dto.request;

public record VectorSearchRequest(
        String query, //사용자 자연어 쿼리
        Integer topK //기본 5개로 설정
) {
}
