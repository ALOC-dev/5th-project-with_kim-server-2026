package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.ResidenceAddressExtractionRequest;
import com.with_kim.aloc_study.dto.response.ResidenceVerificationResponse;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.UserRepository;
import com.with_kim.aloc_study.repository.VerifiedAddressRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import java.io.IOException;
import java.util.Locale;

@Service
public class ResidenceVerificationService {

    private final UserRepository userRepository;
    private final VerifiedAddressRepository verifiedAddressRepository;
    private final S3Client s3Client;
    private final SqsPublisherService sqsPublisherService;
    private final String bucket;

    public ResidenceVerificationService(
            UserRepository userRepository,
            VerifiedAddressRepository verifiedAddressRepository,
            S3Client s3Client,
            SqsPublisherService sqsPublisherService,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.userRepository = userRepository;
        this.verifiedAddressRepository = verifiedAddressRepository;
        this.s3Client = s3Client;
        this.sqsPublisherService = sqsPublisherService;
        this.bucket = bucket;
    }

    @Transactional
    public ResidenceVerificationResponse upload(Long userId, MultipartFile file) {
        Users user = findUser(userId);
        validatePdf(file);

        if (user.getResidentRegistrationS3Key() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "주민등록초본은 한 번만 제출할 수 있습니다."
            );
        }

        String key = "resident-registration/" + userId + "/transcript.pdf";
        uploadToS3(file, key);
        user.registerResidentRegistrationDocument(bucket, key, file.getOriginalFilename());

        sqsPublisherService.publish(
                ResidenceAddressExtractionRequest.of(userId, bucket, key)
        );
        return ResidenceVerificationResponse.of(user, verifiedAddressRepository
                .findAllByUserIdOrderByAddressOrderAsc(userId));
    }

    @Transactional
    public ResidenceVerificationResponse defer(Long userId) {
        Users user = findUser(userId);
        try {
            user.deferResidenceVerification();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }

        return ResidenceVerificationResponse.of(user, verifiedAddressRepository
                .findAllByUserIdOrderByAddressOrderAsc(userId));
    }

    @Transactional(readOnly = true)
    public ResidenceVerificationResponse get(Long userId) {
        Users user = findUser(userId);
        return ResidenceVerificationResponse.of(
                user,
                verifiedAddressRepository.findAllByUserIdOrderByAddressOrderAsc(userId)
        );
    }

    private Users findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("주민등록초본 PDF가 필요합니다.");
        }

        String filename = file.getOriginalFilename();
        boolean pdfFilename = filename != null
                && filename.toLowerCase(Locale.ROOT).endsWith(".pdf");
        boolean pdfContentType = "application/pdf".equalsIgnoreCase(file.getContentType());
        if (!pdfFilename && !pdfContentType) {
            throw new IllegalArgumentException("PDF 파일만 업로드할 수 있습니다.");
        }
    }

    private void uploadToS3(MultipartFile file, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/pdf")
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("주민등록초본 파일을 읽지 못했습니다.", e);
        } catch (Exception e) {
            throw new IllegalStateException("주민등록초본 S3 업로드에 실패했습니다.", e);
        }
    }
}
