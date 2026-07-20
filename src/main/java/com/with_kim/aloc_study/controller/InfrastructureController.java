package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.InfrastructureResponse;
import com.with_kim.aloc_study.service.InfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/infrastructures")
@RequiredArgsConstructor
public class InfrastructureController {

    private final InfrastructureService infrastructureService;

    // 인프라 목록 전체 조회
    @GetMapping
    public List<InfrastructureResponse> getAllInfrastructures() {
        return infrastructureService.getAllInfrastructures();
    }

    // 특정 카테고리 인프라 목록 조회
    @GetMapping(params = "category")
    public List<InfrastructureResponse> getInfrastructuresByCategory(
            @RequestParam String category
    ) {
        return infrastructureService.getInfrastructuresByCategory(category);
    }

    // 인프라 명칭 기준 조회
    @GetMapping("/search")
    public List<InfrastructureResponse> searchByName(
            @RequestParam String name
    ) {
        return infrastructureService.searchByName(name);
    }
}
