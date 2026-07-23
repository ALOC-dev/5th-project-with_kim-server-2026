package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefresh is a Querydsl query type for Refresh
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefresh extends EntityPathBase<Refresh> {

    private static final long serialVersionUID = 1080159978L;

    public static final QRefresh refresh = new QRefresh("refresh");

    public final DateTimePath<java.time.LocalDateTime> expiresAt = createDateTime("expiresAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRefresh(String variable) {
        super(Refresh.class, forVariable(variable));
    }

    public QRefresh(Path<? extends Refresh> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefresh(PathMetadata metadata) {
        super(Refresh.class, metadata);
    }

}

