package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchoolBuilding is a Querydsl query type for SchoolBuilding
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchoolBuilding extends EntityPathBase<SchoolBuilding> {

    private static final long serialVersionUID = 1504699225L;

    public static final QSchoolBuilding schoolBuilding = new QSchoolBuilding("schoolBuilding");

    public final StringPath buildingName = createString("buildingName");

    public final NumberPath<Integer> buildingNumber = createNumber("buildingNumber", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final ComparablePath<org.locationtech.jts.geom.Point> location = createComparable("location", org.locationtech.jts.geom.Point.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public QSchoolBuilding(String variable) {
        super(SchoolBuilding.class, forVariable(variable));
    }

    public QSchoolBuilding(Path<? extends SchoolBuilding> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchoolBuilding(PathMetadata metadata) {
        super(SchoolBuilding.class, metadata);
    }

}

