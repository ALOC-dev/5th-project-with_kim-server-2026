package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsers is a Querydsl query type for Users
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsers extends EntityPathBase<Users> {

    private static final long serialVersionUID = -1689542185L;

    public static final QUsers users = new QUsers("users");

    public final NumberPath<Long> budget = createNumber("budget", Long.class);

    public final StringPath department = createString("department");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath loginId = createString("loginId");

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final StringPath password = createString("password");

    public final NumberPath<Long> preferredDeposit = createNumber("preferredDeposit", Long.class);

    public final NumberPath<Long> preferredSchoolBuildingId = createNumber("preferredSchoolBuildingId", Long.class);

    public final BooleanPath prefersJeonse = createBoolean("prefersJeonse");

    public final BooleanPath prefersMonthlyRent = createBoolean("prefersMonthlyRent");

    public final StringPath residentRegistrationError = createString("residentRegistrationError");

    public final StringPath residentRegistrationOriginalFilename = createString("residentRegistrationOriginalFilename");

    public final StringPath residentRegistrationS3Bucket = createString("residentRegistrationS3Bucket");

    public final StringPath residentRegistrationS3Key = createString("residentRegistrationS3Key");

    public final EnumPath<Users.ResidenceVerificationStatus> residentRegistrationStatus = createEnum("residentRegistrationStatus", Users.ResidenceVerificationStatus.class);

    public final DateTimePath<java.time.LocalDateTime> residentRegistrationUploadedAt = createDateTime("residentRegistrationUploadedAt", java.time.LocalDateTime.class);

    public final EnumPath<Users.Role> role = createEnum("role", Users.Role.class);

    public final ListPath<Submission, QSubmission> submissions = this.<Submission, QSubmission>createList("submissions", Submission.class, QSubmission.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public final ListPath<VerifiedAddress, QVerifiedAddress> verifiedAddresses = this.<VerifiedAddress, QVerifiedAddress>createList("verifiedAddresses", VerifiedAddress.class, QVerifiedAddress.class, PathInits.DIRECT2);

    public QUsers(String variable) {
        super(Users.class, forVariable(variable));
    }

    public QUsers(Path<? extends Users> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUsers(PathMetadata metadata) {
        super(Users.class, metadata);
    }

}

