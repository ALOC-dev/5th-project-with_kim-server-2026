package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisDocument is a Querydsl query type for AnalysisDocument
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisDocument extends EntityPathBase<AnalysisDocument> {

    private static final long serialVersionUID = -1127575320L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisDocument analysisDocument = new QAnalysisDocument("analysisDocument");

    public final NumberPath<Integer> activeEncumbranceCount = createNumber("activeEncumbranceCount", Integer.class);

    public final NumberPath<Integer> activeMortgageCount = createNumber("activeMortgageCount", Integer.class);

    public final QAnalysisResult analysisResult;

    public final StringPath currentOwner = createString("currentOwner");

    public final StringPath docType = createString("docType");

    public final BooleanPath hasDaejigwon = createBoolean("hasDaejigwon");

    public final BooleanPath hasSeparateLandRegistry = createBoolean("hasSeparateLandRegistry");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inferredPropertyType = createString("inferredPropertyType");

    public final StringPath notes = createString("notes");

    public QAnalysisDocument(String variable) {
        this(AnalysisDocument.class, forVariable(variable), INITS);
    }

    public QAnalysisDocument(Path<? extends AnalysisDocument> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisDocument(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisDocument(PathMetadata metadata, PathInits inits) {
        this(AnalysisDocument.class, metadata, inits);
    }

    public QAnalysisDocument(Class<? extends AnalysisDocument> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResult = inits.isInitialized("analysisResult") ? new QAnalysisResult(forProperty("analysisResult"), inits.get("analysisResult")) : null;
    }

}

