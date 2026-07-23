package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.SeoulRealEstateImportResponse;
import com.with_kim.aloc_study.infrastructure.SeoulRealEstateApiClient;
import com.with_kim.aloc_study.service.SeoulRealEstateImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seoul-real-estate")
@RequiredArgsConstructor
@Tag(name = "Seoul Real Estate Import", description = "서울시 부동산 실거래가 OpenAPI")
public class SeoulRealEstateImportController {

    private final SeoulRealEstateImportService importService;

    @PostMapping("/import/latest")
    @Operation(summary = "최신 실거래가 DB 저장", description = "서울시 부동산 실거래가 OpenAPI 호출하여 DB에 저장")
    public SeoulRealEstateImportResponse importLatest(@RequestParam(defaultValue = "1000") int maxRows) {
        int safeMaxRows = Math.max(1, maxRows);
        SeoulRealEstateApiClient.SearchCondition condition = SeoulRealEstateApiClient.SearchCondition.empty();

        return importService.importDeals(condition, 1000, safeMaxRows);
    }

}
