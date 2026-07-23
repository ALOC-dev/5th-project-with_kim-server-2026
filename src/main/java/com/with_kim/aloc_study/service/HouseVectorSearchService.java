package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.HouseSearchResponse;
import com.with_kim.aloc_study.repository.HouseEmbeddingRepository;
import com.with_kim.aloc_study.util.OpenAiEmbeddingClient;
import com.with_kim.aloc_study.util.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseVectorSearchService {
    private static final int DEFAULT_TOP_K = 5;

    private final OpenAiEmbeddingClient embeddingClient;
    private final HouseEmbeddingRepository houseEmbeddingRepository;

    @Transactional(readOnly = true)
    public List<HouseSearchResponse> search(String query,Integer topK){
        int k=(topK==null || topK<=0)? DEFAULT_TOP_K:topK;

        //사용자 쿼리 임베딩
        List<Double> queryVector=embeddingClient.embed(query);

        //가장 비슷한 매물 k개
        return houseEmbeddingRepository
                .searchByVector(VectorUtils.toVectorLiteral(queryVector),k)
                .stream()
                .map(HouseSearchResponse::from)
                .toList();

    }
}
