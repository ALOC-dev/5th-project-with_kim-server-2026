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

    public static final QSubmission submission = new QSubmission("submission");

    public final DateTimePath<java.time.LocalDateTime> analyzedAt = createDateTime("analyzedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> deposit = createNumber("deposit", Long.class);

    public final ListPath<SubmissionDocument, QSubmissionDocument> documents = this.<SubmissionDocument, QSubmissionDocument>createList("documents", SubmissionDocument.class, QSubmissionDocument.class, PathInits.DIRECT2);

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

    public QSubmission(String variable) {
        super(Submission.class, forVariable(variable));
    }

    public QSubmission(Path<? extends Submission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSubmission(PathMetadata metadata) {
        super(Submission.class, metadata);
    }

}

