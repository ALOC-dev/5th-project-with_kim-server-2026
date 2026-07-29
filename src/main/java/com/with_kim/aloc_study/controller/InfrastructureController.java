package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.service.InfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Infrastructure", description = "주변 인프라(편의시설) 조회 API")
@RestController
@RequestMapping("/api/infrastructures")
@RequiredArgsConstructor
public class InfrastructureController {

    private final InfrastructureService infrastructureService;

    @Operation(summary = "인프라 전체 목록 조회", description = "등록된 모든 인프라 시설을 조회합니다.")
    @GetMapping
    public List<InfrastructureResponse> getAllInfrastructures() {
        return infrastructureService.getAllInfrastructures();
    }

    @Operation(summary = "카테고리별 인프라 조회", description = "지정한 카테고리에 해당하는 인프라만 조회합니다.")
    @GetMapping("/category/{category}")
    public List<InfrastructureResponse> getInfrastructuresByCategory(
            @Parameter(description = "인프라 카테고리명 (Infrastructure.InfrastructureCategory Enum 값)")
            @PathVariable String category
    ) {
        return infrastructureService.getInfrastructuresByCategory(category);
    }

    @Operation(summary = "인프라 명칭 검색", description = "인프라 이름으로 시설을 검색합니다.")
    @GetMapping("/search")
    public List<InfrastructureResponse> searchByName(
            @Parameter(description = "검색할 인프라 이름") @RequestParam String name
    ) {
        return infrastructureService.searchByName(name);
    }
}