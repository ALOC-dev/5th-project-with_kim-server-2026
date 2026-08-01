package com.with_kim.aloc_study.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddressMatcher {

    private static final Pattern PARENTHESIZED = Pattern.compile("\\([^)]*\\)");
    private static final Pattern UNIT = Pattern.compile("(?:제\\s*)?(\\d{1,5})\\s*호");
    private static final Pattern BUILDING_DETAIL = Pattern.compile(
            "(?:제\\s*)?\\d{1,5}\\s*(?:동|층|호)\\b"
    );
    private static final Pattern NON_ADDRESS_CHARACTER = Pattern.compile("[^가-힣0-9]");
    private static final Pattern JIBUN_CORE = Pattern.compile(
            "([가-힣]+(?:시|군|구))\\s+"
                    + "([가-힣0-9]+(?:동|읍|면|리|가))\\s+"
                    + "(산\\s*)?(\\d+(?:\\s*-\\s*\\d+)?)"
    );
    private static final Pattern ROAD_CORE = Pattern.compile(
            "([가-힣]+(?:시|군|구))\\s+"
                    + "(?:[가-힣0-9]+(?:동|읍|면|리|가)\\s+)?"
                    + "([가-힣0-9]+(?:대로|로|길))\\s+"
                    + "(\\d+(?:\\s*-\\s*\\d+)?)"
    );

    public boolean matchesAnyBuildingAddress(
            String extractedAddress,
            String buildingJibunAddress,
            String buildingRoadAddress
    ) {
        return matchesBuildingAddress(extractedAddress, buildingJibunAddress)
                || matchesBuildingAddress(extractedAddress, buildingRoadAddress);
    }

    public boolean matchesBuildingAddress(String extractedAddress, String buildingAddress) {
        if (extractedAddress == null || buildingAddress == null
                || extractedAddress.isBlank() || buildingAddress.isBlank()) {
            return false;
        }

        if (normalize(extractedAddress).equals(normalize(buildingAddress))) {
            return true;
        }

        List<String> extractedCores = coreAddressKeys(extractedAddress);
        List<String> buildingCores = coreAddressKeys(buildingAddress);
        return extractedCores.stream().anyMatch(buildingCores::contains);
    }

    public OptionalInt extractUnit(String address) {
        if (address == null) {
            return OptionalInt.empty();
        }

        Matcher matcher = UNIT.matcher(Normalizer.normalize(address, Normalizer.Form.NFKC));
        if (!matcher.find()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(matcher.group(1)));
    }

    private String normalize(String address) {
        String normalized = Normalizer.normalize(address, Normalizer.Form.NFKC)
                .replace("서울특별시", "서울")
                .replace("서울시", "서울");
        normalized = PARENTHESIZED.matcher(normalized).replaceAll(" ");
        normalized = BUILDING_DETAIL.matcher(normalized).replaceAll(" ");
        return NON_ADDRESS_CHARACTER.matcher(normalized).replaceAll("");
    }

    private List<String> coreAddressKeys(String address) {
        String normalized = Normalizer.normalize(address, Normalizer.Form.NFKC)
                .replace("서울특별시", "서울")
                .replace("서울시", "서울");
        normalized = PARENTHESIZED.matcher(normalized).replaceAll(" ");

        List<String> keys = new ArrayList<>();
        Matcher jibun = JIBUN_CORE.matcher(normalized);
        while (jibun.find()) {
            keys.add(normalize(jibun.group()));
        }

        Matcher road = ROAD_CORE.matcher(normalized);
        while (road.find()) {
            keys.add(normalize(road.group()));
        }
        return keys;
    }
}
