package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.ResidenceVerificationResponse;
import com.with_kim.aloc_study.service.ResidenceVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "실거주 인증", description = "주민등록초본 PDF를 통한 과거·현재 주소 및 매물 실거주 인증 API")
public class ResidenceVerificationController {

    private final ResidenceVerificationService residenceVerificationService;

    public ResidenceVerificationController(ResidenceVerificationService residenceVerificationService) {
        this.residenceVerificationService = residenceVerificationService;
    }

    @PostMapping(value = "/api/residence-verifications", consumes = "multipart/form-data")
    @Operation(
            summary = "주민등록초본 업로드",
            description = "인증된 사용자가 주민등록초본 PDF를 한 번 제출합니다. "
                    + "파일은 S3에 저장되고 Lambda가 주소와 거주 연도만 추출합니다. "
                    + "동일 주소의 비연속 연도는 2007, 2009처럼, 명시된 기간은 2007~2010처럼 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "주소 추출 요청 등록 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResidenceVerificationResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": "PENDING",
                                      "uploadedAt": "2026-07-31T10:30:00",
                                      "error": null,
                                      "addresses": []
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "PDF가 없거나 지원하지 않는 파일 형식"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 필요"),
            @ApiResponse(responseCode = "409", description = "이미 주민등록초본을 제출한 사용자")
    })
    public ResponseEntity<ResidenceVerificationResponse> upload(
            Authentication authentication,
            @RequestParam("file")
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "정부24 등에서 발급한 텍스트형 주민등록초본 PDF",
                    required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile file
    ) {
        Long userId = authenticatedUserId(authentication);
        return ResponseEntity.accepted().body(residenceVerificationService.upload(userId, file));
    }

    @PatchMapping("/api/residence-verifications/defer")
    @Operation(
            summary = "실거주 인증 나중에 하기",
            description = "주민등록초본 제출을 나중으로 미룹니다. 최초 상태(NULL) 또는 분석 실패(FAILED) 상태인 사용자만 NOT_SUBMITTED 상태로 변경됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "제출 미루기 처리 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResidenceVerificationResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": "NOT_SUBMITTED",
                                      "uploadedAt": null,
                                      "error": null,
                                      "addresses": []
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "JWT 인증 필요"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 주민등록초본을 제출한 사용자")
    })
    public ResidenceVerificationResponse defer(Authentication authentication) {
        return residenceVerificationService.defer(authenticatedUserId(authentication));
    }

    @GetMapping("/api/residence-verifications")
    @Operation(
            summary = "실거주 인증 결과 조회",
            description = "JWT 사용자 본인의 초본 처리 상태, 주소별 거주 연도, 매물 매칭 결과를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResidenceVerificationResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "status": "COMPLETED",
                              "uploadedAt": "2026-07-31T10:30:00",
                              "error": null,
                              "addresses": [{
                                "id": 1,
                                "rawAddress": "서울특별시 동대문구 회기동 62-8 제502호",
                                "roadAddress": "서울특별시 동대문구 회기로18길 46 제502호",
                                "jibunAddress": "서울특별시 동대문구 회기동 62-8 제502호",
                                "current": true,
                                "residenceYears": ["2007", "2009"],
                                "matchStatus": "MATCHED",
                                "houseId": 12
                              }]
                            }
                            """)
            )
    )
    public ResidenceVerificationResponse get(Authentication authentication) {
        return residenceVerificationService.get(authenticatedUserId(authentication));
    }

    private Long authenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("인증된 사용자 정보가 없습니다.");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("JWT 사용자 식별자가 올바르지 않습니다.", e);
        }
    }
}
