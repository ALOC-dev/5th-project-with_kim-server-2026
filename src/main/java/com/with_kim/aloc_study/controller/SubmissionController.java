package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.SubmissionResponse;
import com.with_kim.aloc_study.entity.Submission;
import com.with_kim.aloc_study.repository.SubmissionRepository;
import com.with_kim.aloc_study.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/**
 * 사용자가 이름/보증금/시세/공시가와 PDF를 함께 제출하는 엔드포인트 +
 * 제출한 건의 진행 상태/결과를 조회하는 엔드포인트.
 *
 * multipart/form-data로 텍스트 필드 + 파일을 한 번에 받는다.
 * (프론트엔드에서 presigned URL로 S3에 직접 올리는 방식이 아니라,
 *  백엔드가 파일을 프록시로 받아 S3 업로드까지 처리하는 구조)
 */
@RestController
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;
    private final JsonMapper jsonMapper;

    public SubmissionController(
            SubmissionService submissionService,
            SubmissionRepository submissionRepository,
            JsonMapper jsonMapper
    ) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.jsonMapper = jsonMapper;
    }

    @PostMapping(value = "/api/submissions", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> submit(
            @RequestParam("owner") String owner,
            @RequestParam("tenantName") String tenantName,
            @RequestParam("deposit") Long deposit,
            @RequestParam(value = "price", required = false) Long price,
            @RequestParam("publicPrice") Long publicPrice,
            @RequestParam("leaseType") Submission.LeaseType leaseType,
            @RequestParam("file") MultipartFile file
    ) {
        // leaseType은 "JEONSE" 또는 "WOLSE" 문자열로 받으며, 스프링이 enum으로 자동 변환한다.
        // (다른 값이 오면 스프링이 알아서 400을 반환. WOLSE는 서비스 레이어에서 400 처리)
        String submissionId = submissionService.submit(owner, tenantName, deposit, price, publicPrice, leaseType, file);

        // 클라이언트는 이 submissionId로 GET /api/submissions/{submissionId}를
        // 폴링해서 분석이 끝났는지(status=ANALYZED) 확인하면 된다.
        return ResponseEntity.accepted().body(Map.of(
                "submissionId", submissionId,
                "status", "QUEUED"
        ));
    }

    /**
     * 제출 건의 현재 상태와, 분석이 끝났다면(status=ANALYZED) 결과까지 함께 반환한다.
     * 프론트에서 짧은 주기로 폴링하거나, 나중에 웹소켓/SSE로 바꿀 수 있는 자리.
     */
    @GetMapping("/api/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable String submissionId) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "submission not found: " + submissionId));

        JsonNode analysis = parseAnalysisJson(submission.getAnalysisResultJson());
        return ResponseEntity.ok(SubmissionResponse.from(submission, analysis));
    }

    private JsonNode parseAnalysisJson(String json) {
        if (json == null) {
            return null; // 아직 분석 전이면 analysisResultJson이 비어있으므로 null 그대로 반환
        }
        // Jackson 3의 JsonMapper는 체크 예외 대신 JacksonException(unchecked)을 던진다.
        return jsonMapper.readTree(json);
    }
}