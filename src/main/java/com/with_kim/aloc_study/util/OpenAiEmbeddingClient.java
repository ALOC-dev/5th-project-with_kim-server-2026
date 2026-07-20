package com.with_kim.aloc_study.util;

import com.with_kim.aloc_study.exception.EmbeddingException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class OpenAiEmbeddingClient {
    private final RestClient restClient;
    private final String model;

    public OpenAiEmbeddingClient(@Qualifier("openAiRestClient")RestClient restClient, @Value("${openai.embedding-model}")String model){
        this.restClient = restClient;
        this.model = model;
    }

    //문장 1개 임베딩
    public List<Double> embed(String text){
        return embedAll(List.of(text)).get(0);
    }

    //문장 여러개 한 번 api호출로 임베딩
    public List<List<Double>> embedAll(List<String>texts){
        try{
            EmbeddingResponse res=restClient.post()
                    .uri("/embeddings")
                    .body(new EmbeddingRequest(model,texts))
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (res == null || res.data() == null || res.data().size() != texts.size()) {
                throw new EmbeddingException("임베딩 응답이 비어있거나 개수가 일치하지 않습니다.");
            }

            return res.data().stream()
                    .sorted((a,b)->Integer.compare(a.index(),b.index()))
                    .map(EmbeddingResponse.Data::embedding)
                    .toList();
        }catch (RestClientException e){
            throw new EmbeddingException("OpenAI 임베딩 API 호출 실패: " + e.getMessage(), e);
        }
    }

    record EmbeddingRequest(String model, List<String> input) {}

    record EmbeddingResponse(List<Data> data) {
        record Data(int index, List<Double> embedding) {}
    }
}
