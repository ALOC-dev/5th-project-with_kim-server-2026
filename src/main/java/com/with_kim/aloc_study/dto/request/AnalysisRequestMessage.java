package com.with_kim.aloc_study.dto.request;

/**
 * Lambda가 SQS에서 받아 처리할 메시지 스키마.
 *
 * S3 이벤트 알림을 거치지 않고 백엔드가 직접 발행하므로,
 * PDF 위치(source)와 계약 컨텍스트(contract)가 이 메시지 하나에
 * 전부 담겨 있다 — Lambda 쪽에서 사이드카 JSON이나 S3 metadata를
 * 추가로 조회할 필요가 없다.
 */
public record AnalysisRequestMessage(
        String submissionId,
        SourceInfo source,
        ContractContext contract
) {

    public record SourceInfo(
            String bucket,
            String key
    ) {}

    public record ContractContext(
            String owner,       // 임대인 이름 — 등기부상 소유자와 일치 여부 판정에 사용
            String tenantName,  // 임차인 이름 — 분석 로직에는 미사용, 메타데이터로만 전달
            Long deposit,
            Long price,
            Long publicPrice
    ) {}
}
