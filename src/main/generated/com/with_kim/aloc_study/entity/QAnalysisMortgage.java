package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisMortgage is a Querydsl query type for AnalysisMortgage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisMortgage extends EntityPathBase<AnalysisMortgage> {

    private static final long serialVersionUID = 2101146729L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisMortgage analysisMortgage = new QAnalysisMortgage("analysisMortgage");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final QAnalysisResult analysisResult;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath jointCollateral = createBoolean("jointCollateral");

    public final NumberPath<Integer> rank = createNumber("rank", Integer.class);

    public final StringPath raw = createString("raw");

    public QAnalysisMortgage(String variable) {
        this(AnalysisMortgage.class, forVariable(variable), INITS);
    }

    public QAnalysisMortgage(Path<? extends AnalysisMortgage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisMortgage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisMortgage(PathMetadata metadata, PathInits inits) {
        this(AnalysisMortgage.class, metadata, inits);
    }

    public QAnalysisMortgage(Class<? extends AnalysisMortgage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResult = inits.isInitialized("analysisResult") ? new QAnalysisResult(forProperty("analysisResult"), inits.get("analysisResult")) : null;
    }

}

