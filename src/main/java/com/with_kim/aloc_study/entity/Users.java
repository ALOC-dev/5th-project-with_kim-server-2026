package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Users {

    public enum Role {
        USER,
        AGENT,
        ADMIN
    }

    public enum ResidenceVerificationStatus {
        NOT_SUBMITTED,
        PENDING,
        COMPLETED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String username;

    private String department;

    private Long preferredSchoolBuildingId;

    private Long preferredDeposit;

    private Long budget;

    private Boolean prefersMonthlyRent;

    private Boolean prefersJeonse;

    private Boolean notificationEnabled;

    @OneToMany(mappedBy = "user")
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<VerifiedAddress> verifiedAddresses = new ArrayList<>();

    @Column(name = "resident_registration_s3_bucket")
    @Setter(AccessLevel.NONE)
    private String residentRegistrationS3Bucket;

    @Column(name = "resident_registration_s3_key", length = 1024)
    @Setter(AccessLevel.NONE)
    private String residentRegistrationS3Key;

    @Column(name = "resident_registration_original_filename")
    @Setter(AccessLevel.NONE)
    private String residentRegistrationOriginalFilename;

    @Column(name = "resident_registration_uploaded_at")
    @Setter(AccessLevel.NONE)
    private LocalDateTime residentRegistrationUploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "resident_registration_status", length = 20)
    @Setter(AccessLevel.NONE)
    private ResidenceVerificationStatus residentRegistrationStatus;

    @Column(name = "resident_registration_error", length = 1000)
    @Setter(AccessLevel.NONE)
    private String residentRegistrationError;

    public void registerResidentRegistrationDocument(
            String s3Bucket,
            String s3Key,
            String originalFilename
    ) {
        if (residentRegistrationS3Key != null) {
            throw new IllegalStateException("주민등록초본은 한 번만 제출할 수 있습니다.");
        }

        this.residentRegistrationS3Bucket = s3Bucket;
        this.residentRegistrationS3Key = s3Key;
        this.residentRegistrationOriginalFilename = originalFilename;
        this.residentRegistrationUploadedAt = LocalDateTime.now();
        this.residentRegistrationStatus = ResidenceVerificationStatus.PENDING;
        this.residentRegistrationError = null;
    }

    public void deferResidenceVerification() {
        if (residentRegistrationStatus != null
                && residentRegistrationStatus != ResidenceVerificationStatus.FAILED) {
            throw new IllegalStateException("주민등록초본 상태가 이미 설정되어 최초 상태 또는 실패 상태에서만 미룰 수 있습니다.");
        }

        this.residentRegistrationStatus = ResidenceVerificationStatus.NOT_SUBMITTED;
        this.residentRegistrationError = null;
    }

    public void completeResidenceVerification() {
        this.residentRegistrationStatus = ResidenceVerificationStatus.COMPLETED;
        this.residentRegistrationError = null;
    }

    public void failResidenceVerification(String error) {
        this.residentRegistrationStatus = ResidenceVerificationStatus.FAILED;
        this.residentRegistrationError = error;
    }

    void addVerifiedAddress(VerifiedAddress verifiedAddress) {
        if (!verifiedAddresses.contains(verifiedAddress)) {
            verifiedAddresses.add(verifiedAddress);
        }
    }

    public void clearVerifiedAddresses() {
        verifiedAddresses.clear();
    }
}
