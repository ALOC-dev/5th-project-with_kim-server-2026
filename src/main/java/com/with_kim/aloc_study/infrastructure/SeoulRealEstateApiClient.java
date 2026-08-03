package com.with_kim.aloc_study.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class SeoulRealEstateApiClient {

    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088";
    private static final String SERVICE_NAME = "tbLnOpendataRtmsV";

    private final RestTemplate restTemplate;

    @Value("${seoul.openapi.key:}")
    private String apiKey;

    public SeoulRealEstateApiResponse fetch(SearchCondition condition, int startIndex, int endIndex) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("seoul.openapi.key 설정이 필요합니다.");
        }

        String url = buildUrl(condition, startIndex, endIndex);

        try {
            SeoulRealEstateApiResponse response = restTemplate.getForObject(url, SeoulRealEstateApiResponse.class);
            if (response == null) {
                throw new IllegalStateException("서울시 실거래가 API 응답이 비어 있습니다.");
            }
            validateResult(response);
            return response;
        } catch (RestClientException e) {
            throw new IllegalStateException("서울시 실거래가 API 요청에 실패했습니다.", e);
        }
    }

    private void validateResult(SeoulRealEstateApiResponse response) {
        String code = response.resultCode();
        if (code == null || "INFO-000".equals(code) || "INFO-200".equals(code)) {
            return;
        }

        throw new IllegalStateException("서울시 실거래가 API 오류: " + code + " - " + response.resultMessage());
    }

    private String buildUrl(SearchCondition condition, int startIndex, int endIndex) {
        List<String> optionalSegments = Arrays.asList(
                condition.receiptYear(),
                condition.cggCode(),
                condition.cggName(),
                condition.stdgCode(),
                condition.lotNoType(),
                condition.lotNoTypeName(),
                condition.mainLotNumber(),
                condition.subLotNumber(),
                condition.buildingName(),
                condition.contractDay(),
                condition.buildingUsage()
        );

        int lastPresentIndex = IntStream.range(0, optionalSegments.size())
                .filter(i -> optionalSegments.get(i) != null && !optionalSegments.get(i).isBlank())
                .max()
                .orElse(-1);

        StringBuilder url = new StringBuilder(BASE_URL)
                .append("/")
                .append(encode(apiKey))
                .append("/json/")
                .append(SERVICE_NAME)
                .append("/")
                .append(startIndex)
                .append("/")
                .append(endIndex);

        if (lastPresentIndex >= 0) {
            optionalSegments.subList(0, lastPresentIndex + 1)
                    .stream()
                    .map(segment -> Objects.toString(segment, ""))
                    .map(this::encode)
                    .forEach(segment -> url.append("/").append(segment));
        }

        return url.append("/").toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public record SearchCondition(
            String receiptYear,
            String cggCode,
            String cggName,
            String stdgCode,
            String lotNoType,
            String lotNoTypeName,
            String mainLotNumber,
            String subLotNumber,
            String buildingName,
            String contractDay,
            String buildingUsage
    ) {
        public static SearchCondition empty() {
            return new SearchCondition(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}
