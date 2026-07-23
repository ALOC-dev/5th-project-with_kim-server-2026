package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisRegistryHit is a Querydsl query type for AnalysisRegistryHit
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisRegistryHit extends EntityPathBase<AnalysisRegistryHit> {

    private static final long serialVersionUID = 1010153257L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisRegistryHit analysisRegistryHit = new QAnalysisRegistryHit("analysisRegistryHit");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final QAnalysisResult analysisResult;

    public final BooleanPath cancelled = createBoolean("cancelled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public final StringPath line = createString("line");

    public final NumberPath<Integer> rank = createNumber("rank", Integer.class);

    public final EnumPath<AnalysisRegistryHit.HitType> type = createEnum("type", AnalysisRegistryHit.HitType.class);

    public QAnalysisRegistryHit(String variable) {
        this(AnalysisRegistryHit.class, forVariable(variable), INITS);
    }

    public QAnalysisRegistryHit(Path<? extends AnalysisRegistryHit> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisRegistryHit(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisRegistryHit(PathMetadata metadata, PathInits inits) {
        this(AnalysisRegistryHit.class, metadata, inits);
    }

    public QAnalysisRegistryHit(Class<? extends AnalysisRegistryHit> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResult = inits.isInitialized("analysisResult") ? new QAnalysisResult(forProperty("analysisResult"), inits.get("analysisResult")) : null;
    }

}

