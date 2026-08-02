package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.Building;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.entity.VerifiedAddress;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.repository.VerifiedAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Service
public class ResidenceVerificationResultService {

    private final UserRepository userRepository;
    private final VerifiedAddressRepository verifiedAddressRepository;
    private final HouseRepository houseRepository;
    private final AddressMatcher addressMatcher;

    public ResidenceVerificationResultService(
            UserRepository userRepository,
            VerifiedAddressRepository verifiedAddressRepository,
            HouseRepository houseRepository,
            AddressMatcher addressMatcher
    ) {
        this.userRepository = userRepository;
        this.verifiedAddressRepository = verifiedAddressRepository;
        this.houseRepository = houseRepository;
        this.addressMatcher = addressMatcher;
    }

    @Transactional
    public void applyResult(Long userId, JsonNode root) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "초본 분석 결과의 사용자를 찾을 수 없습니다: " + userId
                ));

        String status = textOrNull(root, "status");
        if ("FAILED".equalsIgnoreCase(status)) {
            user.failResidenceVerification(textOrDefault(
                    root,
                    "error",
                    "초본에서 주소를 추출하지 못했습니다."
            ));
            return;
        }
        if (!"COMPLETED".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("지원하지 않는 초본 분석 상태입니다: " + status);
        }

        JsonNode addresses = root.path("addresses");
        if (!addresses.isArray()) {
            throw new IllegalArgumentException("초본 분석 결과에 addresses 배열이 없습니다.");
        }

        verifiedAddressRepository.deleteAllByUserId(userId);
        user.clearVerifiedAddresses();

        List<House> houses = houseRepository.findAllWithBuildingForBatch();
        int addressOrder = 0;
        for (JsonNode item : addresses) {
            String rawAddress = requiredText(item, "rawAddress");
            String residenceYears = readYears(item.path("residenceYears"));
            VerifiedAddress address = VerifiedAddress.create(
                    user,
                    rawAddress,
                    textOrNull(item, "roadAddress"),
                    textOrNull(item, "jibunAddress"),
                    item.path("current").asBoolean(false),
                    addressOrder++,
                    residenceYears
            );

            matchAddress(address, houses);
            verifiedAddressRepository.save(address);
        }

        user.completeResidenceVerification();
    }

    private void matchAddress(VerifiedAddress address, List<House> houses) {
        List<House> candidates = houses.stream()
                .filter(house -> matches(address, house))
                .toList();

        if (candidates.isEmpty()) {
            address.markNotFound();
            return;
        }
        if (candidates.size() == 1) {
            address.match(candidates.get(0));
            return;
        }

        OptionalInt unit = firstUnit(address);
        if (unit.isPresent()) {
            List<House> unitCandidates = candidates.stream()
                    .filter(house -> Objects.equals(house.getUnit(), unit.getAsInt()))
                    .toList();
            if (unitCandidates.size() == 1) {
                address.match(unitCandidates.get(0));
                return;
            }
        }
        address.markAmbiguous();
    }

    private boolean matches(VerifiedAddress address, House house) {
        Building building = house.getBuilding();
        if (building == null) {
            return false;
        }

        return candidateAddresses(address).stream().anyMatch(extracted ->
                addressMatcher.matchesAnyBuildingAddress(
                        extracted,
                        building.getAddress(),
                        building.getMAddress()
                )
        );
    }

    private List<String> candidateAddresses(VerifiedAddress address) {
        List<String> values = new ArrayList<>();
        values.add(address.getRawAddress());
        values.add(address.getRoadAddress());
        values.add(address.getJibunAddress());
        return values.stream().filter(Objects::nonNull).filter(value -> !value.isBlank()).toList();
    }

    private OptionalInt firstUnit(VerifiedAddress address) {
        for (String candidate : candidateAddresses(address)) {
            OptionalInt unit = addressMatcher.extractUnit(candidate);
            if (unit.isPresent()) {
                return unit;
            }
        }
        return OptionalInt.empty();
    }

    private String readYears(JsonNode yearsNode) {
        if (!yearsNode.isArray()) {
            throw new IllegalArgumentException("주소별 residenceYears 배열이 없습니다.");
        }

        String years = yearsNode.valueStream()
                .map(JsonNode::asString)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
        if (years.isBlank()) {
            throw new IllegalArgumentException("주소별 거주 연도가 비어 있습니다.");
        }
        return years;
    }

    private String requiredText(JsonNode node, String fieldName) {
        String value = textOrNull(node, fieldName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("초본 분석 결과에 " + fieldName + " 값이 없습니다.");
        }
        return value;
    }

    private String textOrDefault(JsonNode node, String fieldName, String defaultValue) {
        String value = textOrNull(node, fieldName);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String textOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asString();
    }
}
