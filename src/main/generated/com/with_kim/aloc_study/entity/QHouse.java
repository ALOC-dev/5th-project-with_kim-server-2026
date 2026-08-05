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

    public final StringPath analysisLeaseType = createString("analysisLeaseType");

    public final StringPath analysisStatus = createString("analysisStatus");

    public final DateTimePath<java.time.LocalDateTime> analysisUpdatedAt = createDateTime("analysisUpdatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Double> area = createNumber("area", Double.class);

    public final NumberPath<Integer> bldg = createNumber("bldg", Integer.class);

    public final QBuilding building;

    public final EnumPath<House.ContractType> contractType = createEnum("contractType", House.ContractType.class);

    public final NumberPath<Long> deposit = createNumber("deposit", Long.class);

    public final NumberPath<Double> depositRecoveryRate = createNumber("depositRecoveryRate", Double.class);

    public final StringPath description = createString("description");

    public final EnumPath<House.Direction> direction = createEnum("direction", House.Direction.class);

    public final NumberPath<Long> estimatedRecoverableDeposit = createNumber("estimatedRecoverableDeposit", Long.class);

    public final NumberPath<Integer> floor = createNumber("floor", Integer.class);

    public final BooleanPath hugEligible = createBoolean("hugEligible");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image1Url = createString("image1Url");

    public final StringPath image2Url = createString("image2Url");

    public final StringPath image3Url = createString("image3Url");

    public final NumberPath<Double> jeonseRate = createNumber("jeonseRate", Double.class);

    public final BooleanPath lhEligible = createBoolean("lhEligible");

    public final NumberPath<Long> managementFee = createNumber("managementFee", Long.class);

    public final StringPath metadata = createString("metadata");

    public final DateTimePath<java.time.LocalDateTime> metadataUpdatedAt = createDateTime("metadataUpdatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> monthlyRent = createNumber("monthlyRent", Long.class);

    public final NumberPath<Long> mortgageTotal = createNumber("mortgageTotal", Long.class);

    public final NumberPath<Long> number = createNumber("number", Long.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath riskLevel = createString("riskLevel");

    public final NumberPath<Double> riskScore = createNumber("riskScore", Double.class);

    public final NumberPath<Integer> roomNumber = createNumber("roomNumber", Integer.class);

    public final StringPath sourceKey = createString("sourceKey");

    public final ListPath<Submission, QSubmission> submissions = this.<Submission, QSubmission>createList("submissions", Submission.class, QSubmission.class, PathInits.DIRECT2);

    public final NumberPath<Integer> toilet = createNumber("toilet", Integer.class);

    public final NumberPath<Integer> unit = createNumber("unit", Integer.class);

    public final ListPath<VerifiedAddress, QVerifiedAddress> verifiedAddresses = this.<VerifiedAddress, QVerifiedAddress>createList("verifiedAddresses", VerifiedAddress.class, QVerifiedAddress.class, PathInits.DIRECT2);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

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

