package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.request.VectorSearchRequest;
import com.with_kim.aloc_study.dto.response.HouseSearchResponse;
import com.with_kim.aloc_study.service.HouseEmbeddingService;
import com.with_kim.aloc_study.service.HouseVectorSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name="매물 자연어 검색", description = "자연어 검색 및 임베딩 관리 API")
@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseVectorSearchController {
    private final HouseEmbeddingService houseEmbeddingService;
    private final HouseVectorSearchService houseVectorSearchService;

    //전체 매물 임베딩 생성
    @PostMapping("/embeddings/rebuild")
    public Map<String, Integer> rebuild() {
        return Map.of("updated", houseEmbeddingService.rebuildAll());
    }

    //자연어 매물 검색
    @PostMapping("/search")
    public List<HouseSearchResponse> search(@RequestBody VectorSearchRequest request) {
        return houseVectorSearchService.search(request.query(), request.topK());
    }
}
