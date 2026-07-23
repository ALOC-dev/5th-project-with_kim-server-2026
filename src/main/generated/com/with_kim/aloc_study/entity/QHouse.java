package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHouse is a Querydsl query type for House
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHouse extends EntityPathBase<House> {

    private static final long serialVersionUID = -1701651729L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHouse house = new QHouse("house");

    public final NumberPath<Double> area = createNumber("area", Double.class);

    public final NumberPath<Integer> bldg = createNumber("bldg", Integer.class);

    public final QBuilding building;

    public final EnumPath<House.ContractType> contractType = createEnum("contractType", House.ContractType.class);

    public final NumberPath<Long> deposit = createNumber("deposit", Long.class);

    public final StringPath description = createString("description");

    public final EnumPath<House.Direction> direction = createEnum("direction", House.Direction.class);

    public final NumberPath<Integer> floor = createNumber("floor", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image1Url = createString("image1Url");

    public final StringPath image2Url = createString("image2Url");

    public final StringPath image3Url = createString("image3Url");

    public final NumberPath<Long> managementFee = createNumber("managementFee", Long.class);

    public final StringPath metadata = createString("metadata");

    public final DateTimePath<java.time.LocalDateTime> metadataUpdatedAt = createDateTime("metadataUpdatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> monthlyRent = createNumber("monthlyRent", Long.class);

    public final NumberPath<Long> number = createNumber("number", Long.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Integer> roomNumber = createNumber("roomNumber", Integer.class);

    public final StringPath sourceKey = createString("sourceKey");

    public final NumberPath<Integer> toilet = createNumber("toilet", Integer.class);

    public final NumberPath<Integer> unit = createNumber("unit", Integer.class);

    public QHouse(String variable) {
        this(House.class, forVariable(variable), INITS);
    }

    public QHouse(Path<? extends House> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHouse(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHouse(PathMetadata metadata, PathInits inits) {
        this(House.class, metadata, inits);
    }

    public QHouse(Class<? extends House> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.building = inits.isInitialized("building") ? new QBuilding(forProperty("building")) : null;
    }

}

