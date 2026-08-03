package com.with_kim.aloc_study.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressApiResponse {
    private List<Document> documents;

    public boolean hasDocument() {
        return documents != null && !documents.isEmpty();
    }

    public Document firstDocument() {
        return hasDocument() ? documents.get(0) : null;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        private String x;
        private String y;
    }
}
