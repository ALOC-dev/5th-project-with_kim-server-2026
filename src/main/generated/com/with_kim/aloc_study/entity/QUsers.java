package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsers is a Querydsl query type for Users
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsers extends EntityPathBase<Users> {

    private static final long serialVersionUID = -1689542185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUsers users = new QUsers("users");

    public final EnumPath<Users.HousingType> housingType = createEnum("housingType", Users.HousingType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath loginId = createString("loginId");

    public final QSchoolBuilding mainBuilding;

    public final NumberPath<Integer> maxDeposit = createNumber("maxDeposit", Integer.class);

    public final StringPath password = createString("password");

    public final QSchoolBuilding subBuilding;

    public final StringPath username = createString("username");

    public QUsers(String variable) {
        this(Users.class, forVariable(variable), INITS);
    }

    public QUsers(Path<? extends Users> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUsers(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUsers(PathMetadata metadata, PathInits inits) {
        this(Users.class, metadata, inits);
    }

    public QUsers(Class<? extends Users> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mainBuilding = inits.isInitialized("mainBuilding") ? new QSchoolBuilding(forProperty("mainBuilding")) : null;
        this.subBuilding = inits.isInitialized("subBuilding") ? new QSchoolBuilding(forProperty("subBuilding")) : null;
    }

}

