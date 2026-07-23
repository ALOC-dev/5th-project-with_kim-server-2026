package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisResult is a Querydsl query type for AnalysisResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisResult extends EntityPathBase<AnalysisResult> {

    private static final long serialVersionUID = -1647110358L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisResult analysisResult = new QAnalysisResult("analysisResult");

    public final StringPath analysisStatus = createString("analysisStatus");

    public final BooleanPath buildingLandOwnerMatch = createBoolean("buildingLandOwnerMatch");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath currentOwner = createString("currentOwner");

    public final ListPath<AnalysisDocument, QAnalysisDocument> documents = this.<AnalysisDocument, QAnalysisDocument>createList("documents", AnalysisDocument.class, QAnalysisDocument.class, PathInits.DIRECT2);

    public final StringPath housePriceBasis = createString("housePriceBasis");

    public final NumberPath<Long> housePriceUsed = createNumber("housePriceUsed", Long.class);

    public final BooleanPath hugEligible = createBoolean("hugEligible");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath lhEligible = createBoolean("lhEligible");

    public final ListPath<AnalysisMessage, QAnalysisMessage> messages = this.<AnalysisMessage, QAnalysisMessage>createList("messages", AnalysisMessage.class, QAnalysisMessage.class, PathInits.DIRECT2);

    public final ListPath<AnalysisMortgage, QAnalysisMortgage> mortgageItems = this.<AnalysisMortgage, QAnalysisMortgage>createList("mortgageItems", AnalysisMortgage.class, QAnalysisMortgage.class, PathInits.DIRECT2);

    public final NumberPath<Long> mortgageTotal = createNumber("mortgageTotal", Long.class);

    public final BooleanPath ownerMatchesContract = createBoolean("ownerMatchesContract");

    public final StringPath ownerNames = createString("ownerNames");

    public final StringPath propertyType = createString("propertyType");

    public final StringPath rawResultS3Bucket = createString("rawResultS3Bucket");

    public final StringPath rawResultS3Key = createString("rawResultS3Key");

    public final NumberPath<Long> registeredTenantDepositTotal = createNumber("registeredTenantDepositTotal", Long.class);

    public final ListPath<AnalysisRegistryHit, QAnalysisRegistryHit> registryHits = this.<AnalysisRegistryHit, QAnalysisRegistryHit>createList("registryHits", AnalysisRegistryHit.class, QAnalysisRegistryHit.class, PathInits.DIRECT2);

    public final StringPath requiredDocuments = createString("requiredDocuments");

    public final StringPath requiredDocumentsReason = createString("requiredDocumentsReason");

    public final StringPath riskLevel = createString("riskLevel");

    public final NumberPath<Double> riskRatio = createNumber("riskRatio", Double.class);

    public final NumberPath<Double> riskScore = createNumber("riskScore", Double.class);

    public final NumberPath<Long> seniorTenantDepositsUsed = createNumber("seniorTenantDepositsUsed", Long.class);

    public final QSubmission submission;

    public final BooleanPath trustFound = createBoolean("trustFound");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAnalysisResult(String variable) {
        this(AnalysisResult.class, forVariable(variable), INITS);
    }

    public QAnalysisResult(Path<? extends AnalysisResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisResult(PathMetadata metadata, PathInits inits) {
        this(AnalysisResult.class, metadata, inits);
    }

    public QAnalysisResult(Class<? extends AnalysisResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.submission = inits.isInitialized("submission") ? new QSubmission(forProperty("submission")) : null;
    }

}

