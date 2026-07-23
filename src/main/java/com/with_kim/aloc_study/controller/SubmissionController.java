package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.SubmissionResponse;
import com.with_kim.aloc_study.entity.AnalysisResult;
import com.with_kim.aloc_study.entity.Submission;
import com.with_kim.aloc_study.repository.AnalysisResultRepository;
import com.with_kim.aloc_study.repository.SubmissionRepository;
import com.with_kim.aloc_study.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
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
@Tag(name = "등기부 분석", description = "등기부등본 PDF 제출, 토지 등기부 보완, 분석 결과 조회 API")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public SubmissionController(
            SubmissionService submissionService,
            SubmissionRepository submissionRepository,
            AnalysisResultRepository analysisResultRepository
    ) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @PostMapping(value = "/api/submissions", consumes = "multipart/form-data")
    @Operation(
            summary = "등기부 분석 요청",
            description = "임대인·임차인·보증금과 등기부등본 PDF를 제출합니다. "
                    + "접수 후 SQS/Lambda 비동기 분석이 시작되며, 반환된 submissionId로 결과 조회 API를 호출합니다. "
                    + "집합건물은 file 하나만 제출할 수 있고, 일반 건물은 buildingFile과 landFile(또는 landFiles)을 함께 제출할 수 있습니다. "
                    + "propertyType을 비워두면 PDF 내용으로 추론합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "분석 요청이 SQS에 등록됨",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {"submissionId":"sub_8c2dca11b4204c25afd061386ed802c7","status":"QUEUED"}
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "필수값 누락, 숫자 형식 오류, 지원하지 않는 임대·주택 유형 또는 PDF 누락"),
            @ApiResponse(responseCode = "500", description = "S3 업로드 또는 SQS 발행 실패")
    })
    public ResponseEntity<Map<String, String>> submit(
            @Parameter(description = "계약서상 임대인 이름", example = "배경미", required = true)
            @RequestParam("owner") String owner,
            @Parameter(description = "계약서상 임차인 이름", example = "김정묵", required = true)
            @RequestParam("tenantName") String tenantName,
            @Parameter(description = "전세 보증금(원). 쉼표 입력도 가능합니다.", example = "120000000", required = true)
            @RequestParam("deposit") String deposit,
            @Parameter(description = "확인한 주택 시세(원). 없으면 공시가격 또는 PDF 정보로 산정합니다.", example = "210000000")
            @RequestParam(value = "price", required = false) String price,
            @Parameter(description = "공시가격(원). price가 없을 때 주택가격 산정에 사용합니다.", example = "147000000")
            @RequestParam(value = "publicPrice", required = false) String publicPrice,
            @Parameter(description = "알고 있는 선순위 임차보증금 합계(원)", example = "0")
            @RequestParam(value = "seniorTenantDeposits", required = false) String seniorTenantDeposits,
            @Parameter(description = "임대 유형", example = "JEONSE", required = true,
                    schema = @Schema(allowableValues = {"JEONSE", "WOLSE"}))
            @RequestParam("leaseType") String leaseType,
            @Parameter(description = "주택 유형. 생략하면 PDF에서 추론합니다.", example = "COLLECTIVE",
                    schema = @Schema(allowableValues = {
                            "APARTMENT", "ROW_HOUSE", "MULTI_FAMILY", "OFFICETEL",
                            "SINGLE_FAMILY", "MULTI_HOUSEHOLD", "COLLECTIVE"
                    }))
            @RequestParam(value = "propertyType", required = false) String propertyType,
            @Parameter(description = "등기부등본 PDF 한 부. 집합건물 또는 자동 판별에 사용합니다.",
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "일반 건물의 건물 등기부 PDF", content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestParam(value = "buildingFile", required = false) MultipartFile buildingFile,
            @Parameter(description = "일반 건물의 토지 등기부 PDF 한 부", content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestParam(value = "landFile", required = false) MultipartFile landFile,
            @Parameter(description = "일반 건물의 토지 등기부 PDF 여러 부",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestParam(value = "landFiles", required = false) List<MultipartFile> landFiles
    ) {
        String submissionId = submissionService.submit(
                owner,
                tenantName,
                parseRequiredLong(deposit, "deposit"),
                parseOptionalLong(price, "price"),
                parseOptionalLong(publicPrice, "publicPrice"),
                parseOptionalLong(seniorTenantDeposits, "seniorTenantDeposits"),
                parseLeaseType(leaseType),
                parsePropertyType(propertyType),
                file,
                buildingFile,
                landFile,
                landFiles
        );

        // 클라이언트는 이 submissionId로 GET /api/submissions/{submissionId}를
        // 폴링해서 분석이 끝났는지(status=ANALYZED) 확인하면 된다.
        return ResponseEntity.accepted().body(Map.of(
                "submissionId", submissionId,
                "status", "QUEUED"
        ));
    }

    /**
     * 최초 분석에서 토지 등기부가 필요하다고 판단된 제출 건에 PDF를 보완한다.
     * 기존 건물 문서와 이번 토지 문서를 함께 다시 Lambda로 보내 재분석한다.
     */
    @PostMapping(value = "/api/submissions/{submissionId}/land-documents", consumes = "multipart/form-data")
    @Operation(
            summary = "토지 등기부 보완 제출",
            description = "최초 분석 결과의 status가 NEEDS_MORE_DOCS일 때만 사용합니다. "
                    + "기존 건물 등기부와 새 토지 등기부를 함께 다시 분석합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "토지 등기부 보완 요청이 SQS에 등록됨",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {"submissionId":"sub_8c2dca11b4204c25afd061386ed802c7","status":"QUEUED"}
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 submissionId"),
            @ApiResponse(responseCode = "409", description = "토지 등기부 보완이 필요한 상태가 아님"),
            @ApiResponse(responseCode = "400", description = "토지 PDF가 비어 있거나 형식이 올바르지 않음")
    })
    public ResponseEntity<Map<String, String>> addLandDocuments(
            @Parameter(in = ParameterIn.PATH, description = "최초 분석 요청의 submissionId", example = "sub_8c2dca11b4204c25afd061386ed802c7", required = true)
            @PathVariable String submissionId,
            @Parameter(description = "보완할 토지 등기부 PDF. 여러 부를 선택할 수 있습니다.", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestParam("landFiles") List<MultipartFile> landFiles
    ) {
        submissionService.addLandDocuments(submissionId, landFiles);
        return ResponseEntity.accepted().body(Map.of(
                "submissionId", submissionId,
                "status", "QUEUED"
        ));
    }

    private Long parseRequiredLong(String value, String fieldName) {
        Long parsed = parseOptionalLong(value, fieldName);
        if (parsed == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "은 필수입니다.");
        }
        return parsed;
    }

    private Long parseOptionalLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.replace(",", "").replace(" ", ""));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    fieldName + "은 숫자만 입력해야 합니다: " + value);
        }
    }

    private Submission.LeaseType parseLeaseType(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "leaseType은 필수입니다.");
        }
        return switch (value.trim().toUpperCase()) {
            case "JEONSE", "전세" -> Submission.LeaseType.JEONSE;
            case "WOLSE", "월세" -> Submission.LeaseType.WOLSE;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "leaseType은 JEONSE/전세 또는 WOLSE/월세만 가능합니다: " + value);
        };
    }

    private Submission.PropertyType parsePropertyType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "APARTMENT", "아파트" -> Submission.PropertyType.APARTMENT;
            case "ROW_HOUSE", "연립", "연립주택" -> Submission.PropertyType.ROW_HOUSE;
            case "MULTI_FAMILY", "다세대", "다세대주택" -> Submission.PropertyType.MULTI_FAMILY;
            case "OFFICETEL", "오피스텔" -> Submission.PropertyType.OFFICETEL;
            case "SINGLE_FAMILY", "단독", "단독주택" -> Submission.PropertyType.SINGLE_FAMILY;
            case "MULTI_HOUSEHOLD", "다가구", "다가구주택" -> Submission.PropertyType.MULTI_HOUSEHOLD;
            case "COLLECTIVE", "집합건물" -> Submission.PropertyType.COLLECTIVE;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "지원하지 않는 propertyType입니다: " + value);
        };
    }

    /**
     * 제출 건의 현재 상태와, 분석이 끝났다면(status=ANALYZED) 결과까지 함께 반환한다.
     * 프론트에서 짧은 주기로 폴링하거나, 나중에 웹소켓/SSE로 바꿀 수 있는 자리.
     */
    @Transactional(readOnly = true)
    @GetMapping("/api/submissions/{submissionId}")
    @Operation(
            summary = "분석 진행 상태 및 결과 조회",
            description = "QUEUED 상태에서는 분석 대기·진행 중이며 analysis는 null입니다. "
                    + "ANALYZED이면 분석 결과를 반환하고, NEEDS_MORE_DOCS이면 requiredDocuments와 requiredDocumentsReason을 확인해 토지 등기부를 보완 제출합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "제출 상태 또는 분석 결과",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "분석 대기", value = """
                                            {"submissionId":"sub_8c2dca11b4204c25afd061386ed802c7","status":"QUEUED","riskLevel":null,"riskScore":null,"analysis":null}
                                            """),
                                    @ExampleObject(name = "분석 완료", value = """
                                            {"submissionId":"sub_8c2dca11b4204c25afd061386ed802c7","status":"ANALYZED","riskLevel":"WARNING","riskScore":47.0,"analysis":{"analysisStatus":"COMPLETE","propertyType":"COLLECTIVE","currentOwner":"배경미","ownerNames":"배경미","ownerMatchesContract":true,"mortgageTotal":54000000,"riskRatio":0.8455,"riskLevel":"WARNING","hugEligible":true,"lhEligible":true,"flags":["근저당+보증금 비율이 90% 한도에 근접합니다."]}}
                                            """)
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 submissionId")
    })
    public ResponseEntity<SubmissionResponse> getSubmission(
            @Parameter(in = ParameterIn.PATH, description = "분석 요청의 submissionId", example = "sub_8c2dca11b4204c25afd061386ed802c7", required = true)
            @PathVariable String submissionId
    ) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "submission not found: " + submissionId));

        // 토지 등기부 보완 후 재분석 중에는 이전 분석 결과가 DB에 남아 있을 수 있다.
        // 새 결과가 도착하기 전까지는 이를 응답하지 않아, 화면이 오래된 결과를 최신으로
        // 오인하지 않도록 한다.
        AnalysisResult analysis = switch (submission.getStatus()) {
            case ANALYZED, NEEDS_MORE_DOCS -> analysisResultRepository
                    .findBySubmission_SubmissionId(submissionId)
                    .orElse(null);
            default -> null;
        };
        return ResponseEntity.ok(SubmissionResponse.from(submission, analysis));
    }
}
