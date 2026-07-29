package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.entity.AnalysisDocument;
import com.with_kim.aloc_study.entity.AnalysisMessage;
import com.with_kim.aloc_study.entity.AnalysisMortgage;
import com.with_kim.aloc_study.entity.AnalysisRegistryHit;
import com.with_kim.aloc_study.entity.AnalysisResult;

import java.util.Comparator;
import java.util.List;

public record AnalysisResultResponse(
        String analysisStatus,
        String requiredDocuments,
        String requiredDocumentsReason,
        String propertyType,
        List<DocumentResponse> documents,
        String currentOwner,
        String ownerNames,
        Boolean ownerMatchesContract,
        Boolean buildingLandOwnerMatch,
        Boolean trustFound,
        Long mortgageTotal,
        Long seniorTenantDepositsUsed,
        Long registeredTenantDepositTotal,
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
        List<RegistryHitResponse> trustHits,
        List<RegistryHitResponse> landRightHits,
        List<RegistryHitResponse> tenantRightHits
) {

    public static AnalysisResultResponse from(AnalysisResult result) {
        return new AnalysisResultResponse(
                result.getAnalysisStatus(),
                result.getRequiredDocuments(),
                result.getRequiredDocumentsReason(),
                result.getPropertyType(),
                result.getDocuments().stream()
                        .map(DocumentResponse::from)
                        .toList(),
                result.getCurrentOwner(),
                result.getOwnerNames(),
                result.getOwnerMatchesContract(),
                result.getBuildingLandOwnerMatch(),
                result.getTrustFound(),
                result.getMortgageTotal(),
                result.getSeniorTenantDepositsUsed(),
                result.getRegisteredTenantDepositTotal(),
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
                registryHits(result, AnalysisRegistryHit.HitType.TRUST),
                registryHits(result, AnalysisRegistryHit.HitType.LAND_RIGHT),
                registryHits(result, AnalysisRegistryHit.HitType.TENANT_RIGHT)
        );
    }

    private static List<String> messages(AnalysisResult result, AnalysisMessage.MessageType type) {
        return result.getMessages().stream()
                .filter(message -> message.getType() == type)
                .sorted(Comparator.comparing(AnalysisMessage::getDisplayOrder))
                .map(AnalysisMessage::getContent)
                .toList();
    }

    public record DocumentResponse(
            String docType,
            String inferredPropertyType,
            String currentOwner,
            Integer activeMortgageCount,
            Integer activeEncumbranceCount,
            Boolean hasSeparateLandRegistry,
            Boolean hasDaejigwon,
            String notes
    ) {
        public static DocumentResponse from(AnalysisDocument document) {
            return new DocumentResponse(
                    document.getDocType(),
                    document.getInferredPropertyType(),
                    document.getCurrentOwner(),
                    document.getActiveMortgageCount(),
                    document.getActiveEncumbranceCount(),
                    document.getHasSeparateLandRegistry(),
                    document.getHasDaejigwon(),
                    document.getNotes()
            );
        }
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
            Boolean jointCollateral
    ) {
        public static MortgageItemResponse from(AnalysisMortgage mortgage) {
            return new MortgageItemResponse(
                    mortgage.getRank(),
                    mortgage.getRaw(),
                    mortgage.getAmount(),
                    mortgage.getJointCollateral()
            );
        }
    }

    public record RegistryHitResponse(
            String keyword,
            Integer rank,
            String line,
            Long amount,
            Boolean cancelled
    ) {
        public static RegistryHitResponse from(AnalysisRegistryHit hit) {
            return new RegistryHitResponse(
                    hit.getKeyword(),
                    hit.getRank(),
                    hit.getLine(),
                    hit.getAmount(),
                    hit.getCancelled()
            );
        }
    }
}
