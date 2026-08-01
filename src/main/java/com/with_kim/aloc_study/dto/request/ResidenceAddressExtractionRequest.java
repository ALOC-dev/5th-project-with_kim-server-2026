package com.with_kim.aloc_study.dto.request;

public record ResidenceAddressExtractionRequest(
        String messageType,
        Long userId,
        Source source
) {
    public static final String MESSAGE_TYPE = "RESIDENCE_ADDRESS_EXTRACTION";

    public static ResidenceAddressExtractionRequest of(Long userId, String bucket, String key) {
        return new ResidenceAddressExtractionRequest(
                MESSAGE_TYPE,
                userId,
                new Source(bucket, key)
        );
    }

    public record Source(String bucket, String key) {
    }
}
