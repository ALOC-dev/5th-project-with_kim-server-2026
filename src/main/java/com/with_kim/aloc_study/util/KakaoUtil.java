package com.with_kim.aloc_study.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.with_kim.aloc_study.dto.response.KaKaoResponse;
import com.with_kim.aloc_study.exception.KakaoApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Slf4j
public class KakaoUtil {

    @Value("${kakao.client-id}")
    private String client;

    @Value("${kakao.redirect-url}")
    private String redirect;

    @Value("${kakao.client-secret}")
    private String clientSecret;


    ObjectMapper objectMapper = new ObjectMapper();

    public KaKaoResponse.OAuthToken getToken(String responseBody) {
        try {
            KaKaoResponse.OAuthToken oAuthToken =
                    objectMapper.readValue(responseBody, KaKaoResponse.OAuthToken.class);

            log.info("oAuthToken : " + oAuthToken.getAccess_token());

            return oAuthToken;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 토큰 응답 파싱 실패", e);
        }
    }

    public KaKaoResponse.OAuthToken requestToken(String accessCode) {
        try {
            String responseBody = WebClient.create("https://kauth.kakao.com")
                    .post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", client)
                            .with("redirect_uri", redirect)
                            .with("code", accessCode)
                            .with("client_secret", clientSecret))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return getToken(responseBody);
        } catch (WebClientResponseException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 토큰 요청 실패: " + e.getResponseBodyAsString()
            );
        }
    }

    public KaKaoResponse.KakaoProfile requestProfile(String kakaoAccessToken) {
        try {
            String responseBody = WebClient.create("https://kapi.kakao.com")
                    .get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(responseBody, KaKaoResponse.KakaoProfile.class);
        } catch (WebClientResponseException e) {
            throw new KakaoApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "카카오 사용자 정보 요청 실패: " + e.getResponseBodyAsString()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 사용자 정보 응답 파싱 실패", e);
        }
    }
}
