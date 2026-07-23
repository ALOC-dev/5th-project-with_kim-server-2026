package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisMessage is a Querydsl query type for AnalysisMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisMessage extends EntityPathBase<AnalysisMessage> {

    private static final long serialVersionUID = 336564890L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisMessage analysisMessage = new QAnalysisMessage("analysisMessage");

    public final QAnalysisResult analysisResult;

    public final StringPath content = createString("content");

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<AnalysisMessage.MessageType> type = createEnum("type", AnalysisMessage.MessageType.class);

    public QAnalysisMessage(String variable) {
        this(AnalysisMessage.class, forVariable(variable), INITS);
    }

    public QAnalysisMessage(Path<? extends AnalysisMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisMessage(PathMetadata metadata, PathInits inits) {
        this(AnalysisMessage.class, metadata, inits);
    }

    public QAnalysisMessage(Class<? extends AnalysisMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResult = inits.isInitialized("analysisResult") ? new QAnalysisResult(forProperty("analysisResult"), inits.get("analysisResult")) : null;
    }

}

