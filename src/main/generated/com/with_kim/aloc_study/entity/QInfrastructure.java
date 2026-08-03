package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInfrastructure is a Querydsl query type for Infrastructure
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInfrastructure extends EntityPathBase<Infrastructure> {

    private static final long serialVersionUID = -417871180L;

    public static final QInfrastructure infrastructure = new QInfrastructure("infrastructure");

    public final StringPath address = createString("address");

    public final EnumPath<Infrastructure.InfrastructureCategory> category = createEnum("category", Infrastructure.InfrastructureCategory.class);

    public final StringPath ctprvnCd = createString("ctprvnCd");

    public final StringPath emdCd = createString("emdCd");

    public final StringPath external_id = createString("external_id");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final ComparablePath<org.locationtech.jts.geom.Point> location = createComparable("location", org.locationtech.jts.geom.Point.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath mAddress = createString("mAddress");

    public final StringPath name = createString("name");

    public final StringPath sggCd = createString("sggCd");

    public QInfrastructure(String variable) {
        super(Infrastructure.class, forVariable(variable));
    }

    public QInfrastructure(Path<? extends Infrastructure> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInfrastructure(PathMetadata metadata) {
        super(Infrastructure.class, metadata);
    }

}

