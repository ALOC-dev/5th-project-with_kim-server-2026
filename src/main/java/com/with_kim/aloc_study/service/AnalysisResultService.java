package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.AnalysisMessage;
import com.with_kim.aloc_study.entity.AnalysisRegistryHit;
import com.with_kim.aloc_study.entity.AnalysisResult;
import com.with_kim.aloc_study.entity.Submission;
import com.with_kim.aloc_study.repository.AnalysisResultRepository;
import com.with_kim.aloc_study.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

@Service
public class AnalysisResultService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisResultService.class);

    private final SubmissionRepository submissionRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public AnalysisResultService(
            SubmissionRepository submissionRepository,
            AnalysisResultRepository analysisResultRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    /**
     * Lambda가 보낸 분석 결과를 submissionId로 찾은 레코드에 반영한다.
     *
     * SQS는 at-least-once 전달이라 Lambda가 같은 결과를 중복으로 보낼 수 있다.
     * 기존 분석 상세를 비우고 다시 채우므로 같은 결과가 반복 수신되어도 최종 상태는 같다.
     */
    @Transactional
    public void applyResult(
            String submissionId,
            JsonNode analysis,
            String rawResultS3Bucket,
            String rawResultS3Key
    ) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 submissionId: " + submissionId));

        String riskLevel = textOrNull(analysis, "risk_level");
        Double riskScore = doubleOrNull(analysis, "risk_score");
        String analysisStatus = textOrNull(analysis, "analysis_status");

        AnalysisResult result = analysisResultRepository.findBySubmission_SubmissionId(submissionId)
                .orElseGet(() -> new AnalysisResult(submission));

        result.replaceSummary(
                analysisStatus,
                joinTextArray(analysis.path("required_documents")),
                textOrNull(analysis, "required_documents_reason"),
                textOrNull(analysis, "property_type"),
                textOrNull(analysis, "current_owner"),
                joinTextArray(analysis.path("owner_names")),
                booleanOrNull(analysis, "owner_matches_contract"),
                booleanOrNull(analysis, "building_land_owner_match"),
                booleanOrNull(analysis, "trust_found"),
                longOrNull(analysis, "mortgage_total"),
                longOrNull(analysis, "senior_tenant_deposits_used"),
                longOrNull(analysis, "registered_tenant_deposit_total"),
                doubleOrNull(analysis, "risk_ratio"),
                riskScore,
                riskLevel,
                booleanOrNull(analysis, "hug_eligible"),
                booleanOrNull(analysis, "lh_eligible"),
                longOrNull(analysis, "house_price_used"),
                textOrNull(analysis, "house_price_basis"),
                rawResultS3Bucket,
                rawResultS3Key
        );
        result.clearDetails();

        addDocuments(result, analysis.path("documents"));
        addMortgageItems(result, analysis.path("mortgage_items"));
        addMessages(result, AnalysisMessage.MessageType.FLAG, analysis.path("flags"));
        addMessages(result, AnalysisMessage.MessageType.NOTE, analysis.path("notes"));
        addMessages(result, AnalysisMessage.MessageType.HUG_REASON, analysis.path("hug_reasons"));
        addMessages(result, AnalysisMessage.MessageType.LH_REASON, analysis.path("lh_reasons"));
        addRegistryHits(result, AnalysisRegistryHit.HitType.ENCUMBRANCE, analysis.path("encumbrance_hits"));
        addRegistryHits(result, AnalysisRegistryHit.HitType.TRUST, analysis.path("trust_hits"));
        addRegistryHits(result, AnalysisRegistryHit.HitType.LAND_RIGHT, analysis.path("land_right_hits"));
        addRegistryHits(result, AnalysisRegistryHit.HitType.TENANT_RIGHT, analysis.path("tenant_right_hits"));

        analysisResultRepository.save(result);
        submission.applyAnalysisSummary(analysisStatus, riskLevel, riskScore);

        log.info("분석 결과 반영 완료: submissionId={}, analysisStatus={}, riskLevel={}, riskScore={}, mortgages={}, messages={}",
                submissionId, analysisStatus, riskLevel, riskScore,
                result.getMortgageItems().size(), result.getMessages().size());
        // JPA dirty checking으로 트랜잭션 커밋 시점에 UPDATE 쿼리 발생 — save() 재호출 불필요
    }

    private void addDocuments(AnalysisResult result, JsonNode documents) {
        if (!documents.isArray()) {
            return;
        }
        for (JsonNode document : documents) {
            result.addDocument(
                    textOrNull(document, "doc_type"),
                    textOrNull(document, "inferred_property_type"),
                    textOrNull(document, "current_owner"),
                    intOrNull(document, "active_mortgage_count"),
                    intOrNull(document, "active_encumbrance_count"),
                    booleanOrNull(document, "has_separate_land_registry"),
                    booleanOrNull(document, "has_daejigwon"),
                    joinTextArray(document.path("notes"))
            );
        }
    }

    private void addMortgageItems(AnalysisResult result, JsonNode mortgageItems) {
        if (!mortgageItems.isArray()) {
            return;
        }
        for (JsonNode item : mortgageItems) {
            result.addMortgageItem(
                    intOrNull(item, "rank"),
                    textOrNull(item, "raw"),
                    longOrNull(item, "amount"),
                    booleanOrNull(item, "joint_collateral")
            );
        }
    }

    private void addMessages(AnalysisResult result, AnalysisMessage.MessageType type, JsonNode messages) {
        if (!messages.isArray()) {
            return;
        }
        int order = 0;
        for (JsonNode message : messages) {
            if (!message.isNull() && !message.asString().isBlank()) {
                result.addMessage(type, message.asString(), order++);
            }
        }
    }

    private void addRegistryHits(AnalysisResult result, AnalysisRegistryHit.HitType type, JsonNode hits) {
        if (!hits.isArray()) {
            return;
        }
        for (JsonNode hit : hits) {
            result.addRegistryHit(
                    type,
                    textOrNull(hit, "keyword"),
                    intOrNull(hit, "rank"),
                    textOrNull(hit, "line"),
                    longOrNull(hit, "amount"),
                    booleanOrNull(hit, "cancelled")
            );
        }
    }

    private String textOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asString();
    }

    private Integer intOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private Long longOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    private Double doubleOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asDouble();
    }

    private Boolean booleanOrNull(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asBoolean();
    }

    private String joinTextArray(JsonNode values) {
        if (!values.isArray()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (JsonNode value : values) {
            if (value.isNull() || value.asString().isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(value.asString());
        }
        return builder.isEmpty() ? null : builder.toString();
    }
}
