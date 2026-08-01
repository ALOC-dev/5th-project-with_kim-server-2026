package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.entity.VerifiedAddress;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record ResidenceVerificationResponse(
        String status,
        LocalDateTime uploadedAt,
        String error,
        List<Address> addresses
) {
    public static ResidenceVerificationResponse of(
            Users user,
            List<VerifiedAddress> verifiedAddresses
    ) {
        String status = user.getResidentRegistrationStatus() == null
                ? null
                : user.getResidentRegistrationStatus().name();

        return new ResidenceVerificationResponse(
                status,
                user.getResidentRegistrationUploadedAt(),
                user.getResidentRegistrationError(),
                verifiedAddresses.stream().map(Address::from).toList()
        );
    }

    public record Address(
            Long id,
            String rawAddress,
            String roadAddress,
            String jibunAddress,
            boolean current,
            List<String> residenceYears,
            String matchStatus,
            Long houseId
    ) {
        private static Address from(VerifiedAddress address) {
            List<String> years = address.getResidenceYears() == null
                    || address.getResidenceYears().isBlank()
                    ? List.of()
                    : Arrays.stream(address.getResidenceYears().split("\\s*,\\s*"))
                    .filter(value -> !value.isBlank())
                    .toList();

            return new Address(
                    address.getId(),
                    address.getRawAddress(),
                    address.getRoadAddress(),
                    address.getJibunAddress(),
                    address.isCurrent(),
                    years,
                    address.getMatchStatus().name(),
                    address.getHouse() == null ? null : address.getHouse().getId()
            );
        }
    }
}
