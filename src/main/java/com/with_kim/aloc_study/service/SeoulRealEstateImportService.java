package com.with_kim.aloc_study.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.with_kim.aloc_study.dto.response.SeoulRealEstateImportResponse;
import com.with_kim.aloc_study.entity.Building;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.infrastructure.KakaoAddressApiResponse;
import com.with_kim.aloc_study.infrastructure.KakaoApiClient;
import com.with_kim.aloc_study.infrastructure.SeoulRealEstateApiClient;
import com.with_kim.aloc_study.infrastructure.SeoulRealEstateApiResponse;
import com.with_kim.aloc_study.repository.BuildingRepository;
import com.with_kim.aloc_study.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeoulRealEstateImportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final SeoulRealEstateApiClient seoulRealEstateApiClient;
    private final KakaoApiClient kakaoApiClient;
    private final BuildingRepository buildingRepository;
    private final HouseRepository houseRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SeoulRealEstateImportResponse importDeals(SeoulRealEstateApiClient.SearchCondition condition, int pageSize, int maxRows) {
        int requested = 0;
        int imported = 0;
        int duplicated = 0;
        int cancelled = 0;
        int invalid = 0;

        int startIndex = 1;
        int totalCount = Integer.MAX_VALUE;
        Set<String> seenSourceKeys = new HashSet<>();

        while (requested < maxRows && startIndex <= totalCount) {
            int endIndex = Math.min(startIndex + pageSize - 1, startIndex + maxRows - requested - 1);
            SeoulRealEstateApiResponse response = seoulRealEstateApiClient.fetch(condition, startIndex, endIndex);
            totalCount = response.totalCount();

            if (response.rows().isEmpty()) {
                break;
            }

            for (SeoulRealEstateApiResponse.Row row : response.rows()) {
                requested++;

                if (hasText(row.getCancellationDay())) {
                    cancelled++;
                    continue;
                }

                String sourceKey = sourceKey(row);
                if (!hasText(sourceKey) || !isValid(row)) {
                    invalid++;
                    continue;
                }

                if (!seenSourceKeys.add(sourceKey) || houseRepository.existsBySourceKey(sourceKey)) {
                    duplicated++;
                    continue;
                }

                Building building = createBuilding(row);
                House house = House.fromSeoulRtms(
                        building,
                        parseAmountToWon(row.getAmount()),
                        parseDouble(row.getBuildingArea()),
                        parseInteger(row.getFloor()),
                        sourceKey,
                        description(row),
                        metadata(row)
                );

                buildingRepository.save(building);
                houseRepository.save(house);
                imported++;
            }

            startIndex = endIndex + 1;
        }

        return new SeoulRealEstateImportResponse(requested, imported, duplicated, cancelled, invalid);
    }

    private Building createBuilding(SeoulRealEstateApiResponse.Row row) {
        String lotAddress = lotAddress(row);
        Optional<KakaoAddressApiResponse.Document> address = kakaoApiClient.findAddress(lotAddress);

        Double longitude = address.map(KakaoAddressApiResponse.Document::getX)
                .map(this::parseDouble)
                .orElse(null);
        Double latitude = address.map(KakaoAddressApiResponse.Document::getY)
                .map(this::parseDouble)
                .orElse(null);

        return Building.of(
                displayAddress(row),
                lotAddress,
                latitude,
                longitude,
                parseInteger(row.getReceiptYear()),
                row.getCggCode(),
                row.getCggName(),
                row.getStdgCode(),
                row.getStdgName(),
                parseInteger(row.getMainLotNumber()),
                parseInteger(row.getSubLotNumber()),
                parseDate(row.getContractDay()),
                parseAmountToWon(row.getAmount()),
                parseDouble(row.getBuildingArea()),
                parseDouble(row.getLandArea()),
                parseInteger(row.getFloor()),
                parseInteger(row.getConstructionYear()),
                row.getBuildingUsage()
        );
    }

    private boolean isValid(SeoulRealEstateApiResponse.Row row) {
        return parseDate(row.getContractDay()) != null
                && parseAmountToWon(row.getAmount()) != null
                && parseDouble(row.getBuildingArea()) != null;
    }

    private String sourceKey(SeoulRealEstateApiResponse.Row row) {
        return String.join("|",
                text(row.getReceiptYear()),
                text(row.getCggCode()),
                text(row.getStdgCode()),
                text(row.getLotNoType()),
                text(row.getMainLotNumber()),
                text(row.getSubLotNumber()),
                text(row.getBuildingName()),
                text(row.getContractDay()),
                text(row.getAmount()),
                text(row.getBuildingArea()),
                text(row.getFloor()),
                text(row.getBuildingUsage())
        );
    }

    private String lotAddress(SeoulRealEstateApiResponse.Row row) {
        StringBuilder address = new StringBuilder("서울특별시 ")
                .append(text(row.getCggName()))
                .append(" ")
                .append(text(row.getStdgName()))
                .append(" ");

        if ("2".equals(row.getLotNoType()) || "산".equals(row.getLotNoTypeName())) {
            address.append("산 ");
        }

        Integer main = parseInteger(row.getMainLotNumber());
        Integer sub = parseInteger(row.getSubLotNumber());
        if (main != null) {
            address.append(main);
            if (sub != null && sub > 0) {
                address.append("-").append(sub);
            }
        }

        return address.toString().trim();
    }

    private String displayAddress(SeoulRealEstateApiResponse.Row row) {
        String lotAddress = lotAddress(row);
        return hasText(row.getBuildingName()) ? lotAddress + " " + row.getBuildingName() : lotAddress;
    }

    private String description(SeoulRealEstateApiResponse.Row row) {
        return "%s %s 실거래가 %s, 계약일 %s".formatted(
                text(row.getCggName()),
                text(row.getBuildingUsage()),
                text(row.getBuildingName()),
                text(row.getContractDay())
        );
    }

    private String metadata(SeoulRealEstateApiResponse.Row row) {
        try {
            return objectMapper.writeValueAsString(row);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private LocalDate parseDate(String value) {
        if (!hasText(value) || value.length() != 8) {
            return null;
        }

        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private Long parseAmountToWon(String value) {
        Long amount = parseLong(value);
        return amount == null ? null : amount * 10_000L;
    }

    private Long parseLong(String value) {
        if (!hasText(value)) {
            return null;
        }

        try {
            return Long.parseLong(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        Long parsed = parseLong(value);
        return parsed == null ? null : parsed.intValue();
    }

    private Double parseDouble(String value) {
        if (!hasText(value)) {
            return null;
        }

        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
