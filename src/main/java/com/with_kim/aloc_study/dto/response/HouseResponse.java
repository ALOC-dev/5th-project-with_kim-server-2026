package com.with_kim.aloc_study.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.with_kim.aloc_study.dto.MetadataDto;
import com.with_kim.aloc_study.entity.House;

import java.util.List;

public record HouseResponse(
        Long houseId,
        Long buildingId,
        String address,
        Long price,
        Long deposit,
        Long monthlyRent,
        Double area,
        Integer roomNumber,
        Integer toilet,
        Long managementFee,
        String contractType,
        Integer floor,
        String direction,
        String description,
        String analysisStatus,
        String analysisLeaseType,
        String riskLevel,
        Double riskScore,
        Long mortgageTotal,
        Double jeonseRate,
        Boolean lhEligible,
        Boolean hugEligible,
        Long estimatedRecoverableDeposit,
        Double depositRecoveryRate,
        java.time.LocalDateTime analysisUpdatedAt,
        MetadataDto metadata,
        List<String>imageUrls,
        Long viewCount
) {
    public static HouseResponse from(House house) {
        MetadataDto metadata = null;
        if (house.getMetadata() != null) {
            try{
                ObjectMapper mapper = new ObjectMapper();
                metadata = mapper.readValue(house.getMetadata(), MetadataDto.class);
            }catch(JsonProcessingException e){
                metadata = null;
            }
        }

        return new HouseResponse(
                house.getId(),
                house.getBuilding().getId(),
                house.getBuilding().getAddress(),
                house.getPrice(),
                house.getDeposit(),
                house.getMonthlyRent(),
                house.getArea(),
                house.getRoomNumber(),
                house.getToilet(),
                house.getManagementFee(),
                house.getContractType().name(),
                house.getFloor(),
                house.getDirection().name(),
                house.getDescription(),
                house.getAnalysisStatus(),
                house.getAnalysisLeaseType(),
                house.getRiskLevel(),
                house.getRiskScore(),
                house.getMortgageTotal(),
                house.getJeonseRate(),
                house.getLhEligible(),
                house.getHugEligible(),
                house.getEstimatedRecoverableDeposit(),
                house.getDepositRecoveryRate(),
                house.getAnalysisUpdatedAt(),
                metadata,
                house.getImageUrls(),
                house.getViewCount()
        );
    }
}
