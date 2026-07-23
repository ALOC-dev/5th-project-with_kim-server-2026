package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.AnalysisRequestMessage;
import com.with_kim.aloc_study.entity.Submission;
import com.with_kim.aloc_study.entity.SubmissionDocument;
import com.with_kim.aloc_study.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 제출(이름/보증금/시세/공시가 + PDF)을 처리하는 오케스트레이션 서비스.
 *
 * 순서가 중요하다:
 *   1) DB에 제출 정보 저장 (submission_id 확보, status=PENDING)
 *   2) S3에 PDF 업로드
 *   3) 위 두 개가 모두 성공한 경우에만 SQS에 분석 요청 발행 (성공 시 status=QUEUED)
 *
 * 1)이나 2)가 실패하면 SQS 발행 자체를 하지 않으므로,
 * "PDF는 있는데 컨텍스트가 없는" 또는 그 반대의 애매한 상태가
 * 생기지 않는다.
 */
@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final S3Client s3Client;
    private final SqsPublisherService sqsPublisherService;
    private final String bucket;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            S3Client s3Client,
            SqsPublisherService sqsPublisherService,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.submissionRepository = submissionRepository;
        this.s3Client = s3Client;
        this.sqsPublisherService = sqsPublisherService;
        this.bucket = bucket;
    }

    @Transactional
    public String submit(
            String owner,
            String tenantName,
            Long deposit,
            Long price,
            Long publicPrice,
            Long seniorTenantDeposits,
            Submission.LeaseType leaseType,
            Submission.PropertyType propertyType,
            MultipartFile file,
            MultipartFile buildingFile,
            MultipartFile landFile,
            List<MultipartFile> landFiles
    ) {
        // 0) 임대 유형 검증 — 현재는 전세만 분석을 지원한다.
        //    월세는 보증금 구조가 달라(보증금+월차임) 기존 전세가율/HUG 판정 로직을
        //    그대로 적용하면 잘못된 결과가 나오므로, 로직이 준비되기 전까지는
        //    접수 단계에서 명확하게 거부한다. (S3/SQS 리소스도 아끼는 효과)
        if (leaseType != Submission.LeaseType.JEONSE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "현재는 전세 계약만 분석을 지원합니다. 월세 지원은 준비 중입니다.");
        }

        // price(시세)는 선택값이다 — 없으면 analyzer.py가 공시가격×1.4로 자동 환산해서
        // 주택가격을 계산한다(HUG 산정 방식). 다만 둘 다 없으면 주택가격 산정
        // 근거 자체가 없어서 위험도 계산이 무의미해지므로, 그 경우만 여기서 막는다.
        if (price == null && publicPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "시세(price) 또는 공시가격(publicPrice) 중 최소 하나는 입력해야 합니다.");
        }

        String submissionId = "sub_" + UUID.randomUUID().toString().replace("-", "");

        // 1) DB 저장 — 여기서 실제로 INSERT가 일어난다.
        Submission submission = new Submission(
                submissionId, owner, tenantName, deposit, price, publicPrice, seniorTenantDeposits,
                leaseType, propertyType, bucket, "uploads/" + submissionId + ".pdf");
        submissionRepository.save(submission);
        log.info("DB 저장 완료: submissionId={}", submissionId);

        // 2) S3 업로드
        List<AnalysisRequestMessage.SourceInfo> sources = uploadSources(
                submissionId, submission, file, buildingFile, landFile, landFiles);

        // 3) SQS 발행 — 위 두 단계가 모두 성공했을 때만 실행된다.
        AnalysisRequestMessage message = new AnalysisRequestMessage(
                submissionId,
                propertyType == null ? null : propertyType.name(),
                sources,
                new AnalysisRequestMessage.ContractContext(
                        owner, tenantName, deposit, price, publicPrice, seniorTenantDeposits)
        );
        sqsPublisherService.publish(message);

        // 발행까지 성공했으면 상태를 QUEUED로 갱신
        submission.updateStatus(Submission.SubmissionStatus.QUEUED);
        // JPA 영속성 컨텍스트 안에서 변경했으므로, 트랜잭션 커밋 시점에
        // 자동으로 UPDATE 쿼리가 나간다 (dirty checking) — save() 재호출 불필요

        return submissionId;
    }

    /**
     * 토지 등기부 보완이 필요한 기존 제출 건에 토지 PDF를 추가하고 재분석을 요청한다.
     * Lambda에는 기존 건물 문서와 새 토지 문서를 모두 전달해야 하므로, 새 파일만이 아니라
     * submission_documents 전체를 SQS 메시지의 sources로 다시 구성한다.
     */
    @Transactional
    public void addLandDocuments(String submissionId, List<MultipartFile> landFiles) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "submission not found: " + submissionId));

        if (submission.getStatus() != Submission.SubmissionStatus.NEEDS_MORE_DOCS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "토지 등기부 보완이 필요한 상태가 아닙니다: " + submission.getStatus());
        }

        int nextLandNumber = (int) submission.getDocuments().stream()
                .filter(document -> document.getDocHint() == SubmissionDocument.DocHint.LAND)
                .count() + 1;
        int uploadedCount = 0;
        if (landFiles != null) {
            for (MultipartFile landFile : landFiles) {
                if (!hasFile(landFile)) {
                    continue;
                }
                String key = "uploads/" + submissionId + "_land_" + nextLandNumber++ + ".pdf";
                uploadDocument(submission, landFile, key, SubmissionDocument.DocHint.LAND);
                uploadedCount++;
            }
        }

        if (uploadedCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "재분석할 토지 등기부 PDF가 필요합니다.");
        }

        AnalysisRequestMessage message = new AnalysisRequestMessage(
                submissionId,
                submission.getPropertyType() == null ? null : submission.getPropertyType().name(),
                sourcesFromDocuments(submission),
                new AnalysisRequestMessage.ContractContext(
                        submission.getOwner(),
                        submission.getTenantName(),
                        submission.getDeposit(),
                        submission.getPrice(),
                        submission.getPublicPrice(),
                        submission.getSeniorTenantDeposits())
        );
        sqsPublisherService.publish(message);
        submission.updateStatus(Submission.SubmissionStatus.QUEUED);
        log.info("토지 등기부 보완 후 재분석 요청: submissionId={}, landFiles={}",
                submissionId, uploadedCount);
    }

    private List<AnalysisRequestMessage.SourceInfo> uploadSources(
            String submissionId,
            Submission submission,
            MultipartFile file,
            MultipartFile buildingFile,
            MultipartFile landFile,
            List<MultipartFile> landFiles
    ) {
        List<AnalysisRequestMessage.SourceInfo> sources = new ArrayList<>();

        if (hasFile(file)) {
            String key = "uploads/" + submissionId + ".pdf";
            uploadSource(submission, sources, file, key, null);
        }
        if (hasFile(buildingFile)) {
            String key = "uploads/" + submissionId + "_building.pdf";
            uploadSource(submission, sources, buildingFile, key, SubmissionDocument.DocHint.BUILDING);
        }
        List<MultipartFile> allLandFiles = new ArrayList<>();
        if (hasFile(landFile)) {
            allLandFiles.add(landFile);
        }
        if (landFiles != null) {
            for (MultipartFile candidate : landFiles) {
                if (hasFile(candidate)) {
                    allLandFiles.add(candidate);
                }
            }
        }
        for (int index = 0; index < allLandFiles.size(); index++) {
            String key = "uploads/" + submissionId + "_land_" + (index + 1) + ".pdf";
            uploadSource(submission, sources, allLandFiles.get(index), key, SubmissionDocument.DocHint.LAND);
        }

        if (sources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "분석할 PDF 파일이 필요합니다. file 또는 buildingFile/landFile/landFiles 중 하나 이상을 업로드하세요.");
        }

        return sources;
    }

    private void uploadSource(
            Submission submission,
            List<AnalysisRequestMessage.SourceInfo> sources,
            MultipartFile pdfFile,
            String key,
            SubmissionDocument.DocHint docHint
    ) {
        sources.add(uploadDocument(submission, pdfFile, key, docHint));
    }

    private AnalysisRequestMessage.SourceInfo uploadDocument(
            Submission submission,
            MultipartFile pdfFile,
            String key,
            SubmissionDocument.DocHint docHint
    ) {
        uploadToS3(key, pdfFile);
        submission.addDocument(new SubmissionDocument(
                submission,
                docHint,
                bucket,
                key,
                pdfFile.getOriginalFilename()
        ));
        log.info("S3 업로드 완료: bucket={}, key={}, docHint={}", bucket, key, docHint);
        return new AnalysisRequestMessage.SourceInfo(
                bucket,
                key,
                docHint == null ? null : docHint.name()
        );
    }

    private List<AnalysisRequestMessage.SourceInfo> sourcesFromDocuments(Submission submission) {
        if (submission.getDocuments().isEmpty()) {
            return List.of(new AnalysisRequestMessage.SourceInfo(
                    submission.getS3Bucket(), submission.getS3Key(), null));
        }

        return submission.getDocuments().stream()
                .map(document -> new AnalysisRequestMessage.SourceInfo(
                        document.getS3Bucket(),
                        document.getS3Key(),
                        document.getDocHint() == null ? null : document.getDocHint().name()))
                .toList();
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private void uploadToS3(String key, MultipartFile pdfFile) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    pdfFile.getInputStream(), pdfFile.getSize()));

        } catch (IOException e) {
            throw new IllegalStateException("PDF 파일을 읽는 데 실패했습니다", e);
        } catch (Exception e) {
            throw new IllegalStateException("S3 업로드 실패: key=" + key, e);
        }
    }
}
