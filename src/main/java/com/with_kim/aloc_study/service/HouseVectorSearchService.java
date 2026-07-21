package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.HouseSearchFilter;
import com.with_kim.aloc_study.dto.response.HouseSearchResponse;
import com.with_kim.aloc_study.repository.HouseEmbeddingRepository;
import com.with_kim.aloc_study.util.OpenAiEmbeddingClient;
import com.with_kim.aloc_study.util.QueryParserClient;
import com.with_kim.aloc_study.util.VectorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseVectorSearchService {
    private static final int DEFAULT_TOP_K = 5;

    private final OpenAiEmbeddingClient embeddingClient;
    private final HouseEmbeddingRepository houseEmbeddingRepository;
    private final QueryParserClient queryParserClient;

    @Transactional(readOnly = true)
    public List<HouseSearchResponse> search(String query,Integer topK){
        int k=(topK==null || topK<=0)? DEFAULT_TOP_K:topK;

        //LLM으로 구조화 필터
        HouseSearchFilter filter=queryParserClient.parse(query);
        log.info("PARSE RESULT: query={}, filter={}", query, filter);

        //사용자 쿼리 임베딩
        //semanticQuery있음->애매한 조건들만 임베딩
        //semanticQuery 없음->원본 문장 임베딩(그냥 혹시 모르니까 나중에 쓸까봐)
        String embedTarget=(filter.semanticQuery()==null || filter.semanticQuery().isBlank())
                ? query: filter.semanticQuery();
        List<Double> queryVector=embeddingClient.embed(embedTarget);

        //가장 비슷한 매물 k개
        return houseEmbeddingRepository
                .searchByVector(VectorUtils.toVectorLiteral(queryVector),filter,k)
                .stream()
                .map(HouseSearchResponse::from)
                .toList();

    }
}
