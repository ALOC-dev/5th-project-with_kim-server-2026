package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubmissionDocument is a Querydsl query type for SubmissionDocument
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubmissionDocument extends EntityPathBase<SubmissionDocument> {

    private static final long serialVersionUID = -417646312L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubmissionDocument submissionDocument = new QSubmissionDocument("submissionDocument");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<SubmissionDocument.DocHint> docHint = createEnum("docHint", SubmissionDocument.DocHint.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalFilename = createString("originalFilename");

    public final StringPath s3Bucket = createString("s3Bucket");

    public final StringPath s3Key = createString("s3Key");

    public final QSubmission submission;

    public QSubmissionDocument(String variable) {
        this(SubmissionDocument.class, forVariable(variable), INITS);
    }

    public QSubmissionDocument(Path<? extends SubmissionDocument> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubmissionDocument(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubmissionDocument(PathMetadata metadata, PathInits inits) {
        this(SubmissionDocument.class, metadata, inits);
    }

    public QSubmissionDocument(Class<? extends SubmissionDocument> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.submission = inits.isInitialized("submission") ? new QSubmission(forProperty("submission")) : null;
    }

}

