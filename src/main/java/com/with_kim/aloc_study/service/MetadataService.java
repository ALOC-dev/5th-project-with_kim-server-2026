package com.with_kim.aloc_study.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.with_kim.aloc_study.dto.MetadataDto;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.infrastructure.KakaoApiClient;
import com.with_kim.aloc_study.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    private final KakaoApiClient kakaoApiClient;
    private final HouseRepository houseRepository;
    private final ObjectMapper objectMapper;

    private static final int RADIUS_METERS = 500;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateMetadataIfNeeded(House house) {
        if (!house.needsMetadataUpdate()) return;

        Double lat = house.getBuilding().getLatitude();
        Double lng = house.getBuilding().getLongitude();

        try {
            CompletableFuture<Integer> mart =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "MT1", RADIUS_METERS));
            CompletableFuture<Integer> convenienceStore =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "CS2", RADIUS_METERS));
            CompletableFuture<Integer> parking =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "PK6", RADIUS_METERS));
            CompletableFuture<Integer> subway =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "SW8", RADIUS_METERS));
            CompletableFuture<Integer> bank =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "BK9", RADIUS_METERS));
            CompletableFuture<Integer> PO =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "PO3", RADIUS_METERS));
            CompletableFuture<Integer> restaurant =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "FD6", RADIUS_METERS));
            CompletableFuture<Integer> cafe =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "CE7", RADIUS_METERS));
            CompletableFuture<Integer> hospital =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "HP8", RADIUS_METERS));
            CompletableFuture<Integer> pharmacy =
                    CompletableFuture.supplyAsync(() -> kakaoApiClient.countByCategory(lat, lng, "PM9", RADIUS_METERS));

            CompletableFuture.allOf(mart, convenienceStore, parking, subway, bank, PO, restaurant, cafe, hospital, pharmacy).join();

            MetadataDto metadata = new MetadataDto(mart.get(), convenienceStore.get(), parking.get(), subway.get(), bank.get(),
                    PO.get(), restaurant.get(), cafe.get(), hospital.get(), pharmacy.get());

            String metadataJson = objectMapper.writeValueAsString(metadata);
            houseRepository.updateMetadata(house.getId(), metadataJson, LocalDateTime.now());
            house.updateMetadata(metadataJson);

        } catch (Exception e) {
            logger.error(
                    "메타데이터 갱신 실패. houseId={}, latitude={}, longitude={}",
                    house.getId(),
                    lat,
                    lng,
                    e
            );
        }
    }
}
