package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.Submission;
import com.with_kim.aloc_study.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class AnalysisResultService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisResultService.class);

    private final SubmissionRepository submissionRepository;
    private final JsonMapper jsonMapper;

    // JsonMapper는 Spring Boot 4가 Jackson 3 기반으로 자동 등록해주는 빈이라
    // 별도 설정 없이 그대로 주입받으면 된다.
    public AnalysisResultService(SubmissionRepository submissionRepository, JsonMapper jsonMapper) {
        this.submissionRepository = submissionRepository;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Lambda가 보낸 분석 결과를 submissionId로 찾은 레코드에 반영한다.
     *
     * SQS는 at-least-once 전달이라 Lambda가 같은 결과를 중복으로 보낼 수 있다.
     * applyAnalysisResult가 몇 번 호출되든 최종 상태는 같은 값으로 덮어써지므로
     * (멱등) 별도의 중복 방지 로직 없이도 안전하다.
     */
    @Transactional
    public void applyResult(String submissionId, JsonNode analysis) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 submissionId: " + submissionId));

        String riskLevel = analysis.path("risk_level").isMissingNode()
                ? null : analysis.path("risk_level").asString();
        Double riskScore = analysis.path("risk_score").isMissingNode()
                ? null : analysis.path("risk_score").asDouble();

        // Jackson 3의 JsonMapper는 체크 예외 대신 JacksonException(unchecked)을 던지므로
        // try/catch로 감쌀 필요가 없어졌다 (필요하면 JacksonException만 잡으면 됨).
        String resultJson = jsonMapper.writeValueAsString(analysis);
        submission.applyAnalysisResult(resultJson, riskLevel, riskScore);

        log.info("분석 결과 반영 완료: submissionId={}, riskLevel={}, riskScore={}",
                submissionId, riskLevel, riskScore);
        // JPA dirty checking으로 트랜잭션 커밋 시점에 UPDATE 쿼리 발생 — save() 재호출 불필요
    }
}
