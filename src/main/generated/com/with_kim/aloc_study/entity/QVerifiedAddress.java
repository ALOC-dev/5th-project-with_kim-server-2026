package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVerifiedAddress is a Querydsl query type for VerifiedAddress
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVerifiedAddress extends EntityPathBase<VerifiedAddress> {

    private static final long serialVersionUID = -982302437L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVerifiedAddress verifiedAddress = new QVerifiedAddress("verifiedAddress");

    public final NumberPath<Integer> addressOrder = createNumber("addressOrder", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final BooleanPath current = createBoolean("current");

    public final QHouse house;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jibunAddress = createString("jibunAddress");

    public final EnumPath<VerifiedAddress.MatchStatus> matchStatus = createEnum("matchStatus", VerifiedAddress.MatchStatus.class);

    public final StringPath rawAddress = createString("rawAddress");

    public final StringPath residenceYears = createString("residenceYears");

    public final StringPath roadAddress = createString("roadAddress");

    public final QUsers user;

    public QVerifiedAddress(String variable) {
        this(VerifiedAddress.class, forVariable(variable), INITS);
    }

    public QVerifiedAddress(Path<? extends VerifiedAddress> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVerifiedAddress(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVerifiedAddress(PathMetadata metadata, PathInits inits) {
        this(VerifiedAddress.class, metadata, inits);
    }

    public QVerifiedAddress(Class<? extends VerifiedAddress> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.house = inits.isInitialized("house") ? new QHouse(forProperty("house"), inits.get("house")) : null;
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user")) : null;
    }

}

