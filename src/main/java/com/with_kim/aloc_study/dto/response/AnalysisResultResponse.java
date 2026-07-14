package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.AnalysisMessage;
import com.with_kim.aloc_study.entity.AnalysisMortgage;
import com.with_kim.aloc_study.entity.AnalysisRegistryHit;
import com.with_kim.aloc_study.entity.AnalysisResult;

import java.util.Comparator;
import java.util.List;

public record AnalysisResultResponse(
        String currentOwner,
        String ownerNames,
        Boolean ownerMatchesContract,
        Boolean trustFound,
        Long mortgageTotal,
        List<MortgageItemResponse> mortgageItems,
        Double riskRatio,
        Double riskScore,
        String riskLevel,
        Boolean hugEligible,
        List<String> hugReasons,
        Boolean lhEligible,
        List<String> lhReasons,
        Long housePriceUsed,
        String housePriceBasis,
        String rawResultS3Bucket,
        String rawResultS3Key,
        List<String> flags,
        List<String> notes,
        List<RegistryHitResponse> encumbranceHits,
        List<RegistryHitResponse> trustHits
) {

    public static AnalysisResultResponse from(AnalysisResult result) {
        return new AnalysisResultResponse(
                result.getCurrentOwner(),
                result.getOwnerNames(),
                result.getOwnerMatchesContract(),
                result.getTrustFound(),
                result.getMortgageTotal(),
                result.getMortgageItems().stream()
                        .sorted(Comparator.comparing(
                                AnalysisMortgage::getRank,
                                Comparator.nullsLast(Integer::compareTo)
                        ))
                        .map(MortgageItemResponse::from)
                        .toList(),
                result.getRiskRatio(),
                result.getRiskScore(),
                result.getRiskLevel(),
                result.getHugEligible(),
                messages(result, AnalysisMessage.MessageType.HUG_REASON),
                result.getLhEligible(),
                messages(result, AnalysisMessage.MessageType.LH_REASON),
                result.getHousePriceUsed(),
                result.getHousePriceBasis(),
                result.getRawResultS3Bucket(),
                result.getRawResultS3Key(),
                messages(result, AnalysisMessage.MessageType.FLAG),
                messages(result, AnalysisMessage.MessageType.NOTE),
                registryHits(result, AnalysisRegistryHit.HitType.ENCUMBRANCE),
                registryHits(result, AnalysisRegistryHit.HitType.TRUST)
        );
    }

    private static List<String> messages(AnalysisResult result, AnalysisMessage.MessageType type) {
        return result.getMessages().stream()
                .filter(message -> message.getType() == type)
                .sorted(Comparator.comparing(AnalysisMessage::getDisplayOrder))
                .map(AnalysisMessage::getContent)
                .toList();
    }

    private static List<RegistryHitResponse> registryHits(AnalysisResult result, AnalysisRegistryHit.HitType type) {
        return result.getRegistryHits().stream()
                .filter(hit -> hit.getType() == type)
                .sorted(Comparator.comparing(
                        AnalysisRegistryHit::getRank,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .map(RegistryHitResponse::from)
                .toList();
    }

    public record MortgageItemResponse(
            Integer rank,
            String raw,
            Long amount,
            String status,
            Boolean jointCollateral
    ) {
        public static MortgageItemResponse from(AnalysisMortgage mortgage) {
            return new MortgageItemResponse(
                    mortgage.getRank(),
                    mortgage.getRaw(),
                    mortgage.getAmount(),
                    mortgage.getStatus(),
                    mortgage.getJointCollateral()
            );
        }
    }

    public record RegistryHitResponse(
            String keyword,
            Integer rank,
            String line,
            Boolean cancelled
    ) {
        public static RegistryHitResponse from(AnalysisRegistryHit hit) {
            return new RegistryHitResponse(
                    hit.getKeyword(),
                    hit.getRank(),
                    hit.getLine(),
                    hit.getCancelled()
            );
        }
    }
}
