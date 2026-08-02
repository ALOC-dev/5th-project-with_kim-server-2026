package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubmission is a Querydsl query type for Submission
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubmission extends EntityPathBase<Submission> {

    private static final long serialVersionUID = -603967267L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubmission submission = new QSubmission("submission");

    public final StringPath address = createString("address");

    public final DateTimePath<java.time.LocalDateTime> analyzedAt = createDateTime("analyzedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> deposit = createNumber("deposit", Long.class);

    public final ListPath<SubmissionDocument, QSubmissionDocument> documents = this.<SubmissionDocument, QSubmissionDocument>createList("documents", SubmissionDocument.class, QSubmissionDocument.class, PathInits.DIRECT2);

    public final QHouse house;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Submission.LeaseType> leaseType = createEnum("leaseType", Submission.LeaseType.class);

    public final StringPath owner = createString("owner");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final EnumPath<Submission.PropertyType> propertyType = createEnum("propertyType", Submission.PropertyType.class);

    public final NumberPath<Long> publicPrice = createNumber("publicPrice", Long.class);

    public final StringPath riskLevel = createString("riskLevel");

    public final NumberPath<Double> riskScore = createNumber("riskScore", Double.class);

    public final StringPath s3Bucket = createString("s3Bucket");

    public final StringPath s3Key = createString("s3Key");

    public final NumberPath<Long> seniorTenantDeposits = createNumber("seniorTenantDeposits", Long.class);

    public final EnumPath<Submission.SubmissionStatus> status = createEnum("status", Submission.SubmissionStatus.class);

    public final StringPath submissionId = createString("submissionId");

    public final StringPath tenantName = createString("tenantName");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUsers user;

    public QSubmission(String variable) {
        this(Submission.class, forVariable(variable), INITS);
    }

    public QSubmission(Path<? extends Submission> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubmission(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubmission(PathMetadata metadata, PathInits inits) {
        this(Submission.class, metadata, inits);
    }

    public QSubmission(Class<? extends Submission> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.house = inits.isInitialized("house") ? new QHouse(forProperty("house"), inits.get("house")) : null;
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user")) : null;
    }

}

