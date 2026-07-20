package com.with_kim.aloc_study.infrastructure;

import com.with_kim.aloc_study.exception.KakaoApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String apiKey;

    private static final String KAKAO_CATEGORY_URL =
            "https://dapi.kakao.com/v2/local/search/category.json";

    public int countByCategory(Double lat, Double lng, String categoryCode, int radiusMeters) {
        String url = KAKAO_CATEGORY_URL
                + "?category_group_code=" + categoryCode
                + "&x=" + lng
                + "&y=" + lat
                + "&radius=" + radiusMeters;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        try {
            ResponseEntity<KakaoPlaceApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KakaoPlaceApiResponse.class
            );

            return response.getBody() != null
                    ? response.getBody().getMeta().getTotalCount()
                    : 0;

        } catch (HttpClientErrorException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 API 요청 오류: " + e.getResponseBodyAsString()
            );

        } catch (HttpServerErrorException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 API 서버 오류"
            );

        } catch (RestClientException e) {
            throw new KakaoApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "카카오 API 통신 실패"
            );
        }
    }
} //uricomponentbuilder, webclient >> 최신 기법, 학습 필요