package com.with_kim.aloc_study.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class HouseDescriptionGenerator {
    private final RestClient restClient;
    private final String model;

    public HouseDescriptionGenerator(@Qualifier("openAiRestClient") RestClient restClient,@Value("${openai.chat-model}") String model){
        this.restClient=restClient;
        this.model=model;
    }

    public String generate(String featureText){
        ChatResponse res=restClient.post()
                .uri("/chat/completions")
                .body(new ChatRequest(model, List.of(
                        new Message("user","다음 매물 정보로 2~3문장 매물 설명을 써줘. 없는 정보는 지어내지 마.\n" + featureText)
                )))
                .retrieve()
                .body(ChatResponse.class);
        return res.choices().get(0).message().content();
    }

    record ChatRequest(String model, List<Message> messages) {}
    record Message(String role, String content) {}
    record ChatResponse(List<Choice> choices) {
        record Choice(Message message) {}
    }
}
