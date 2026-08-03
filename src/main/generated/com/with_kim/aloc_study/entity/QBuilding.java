package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBuilding is a Querydsl query type for Building
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBuilding extends EntityPathBase<Building> {

    private static final long serialVersionUID = -1594476411L;

    public static final QBuilding building = new QBuilding("building");

    public final StringPath address = createString("address");

    public final NumberPath<Double> buildingArea = createNumber("buildingArea", Double.class);

    public final StringPath buildingUsage = createString("buildingUsage");

    public final NumberPath<Integer> constructionYear = createNumber("constructionYear", Integer.class);

    public final DatePath<java.time.LocalDate> contractDate = createDate("contractDate", java.time.LocalDate.class);

    public final StringPath emdCd = createString("emdCd");

    public final StringPath emdName = createString("emdName");

    public final NumberPath<Integer> floor = createNumber("floor", Integer.class);

    public final ListPath<House, QHouse> houses = this.<House, QHouse>createList("houses", House.class, QHouse.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> landArea = createNumber("landArea", Double.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final ComparablePath<org.locationtech.jts.geom.Point> location = createComparable("location", org.locationtech.jts.geom.Point.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath mAddress = createString("mAddress");

    public final NumberPath<Integer> mainLotNumber = createNumber("mainLotNumber", Integer.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Integer> receiptYear = createNumber("receiptYear", Integer.class);

    public final StringPath sggCd = createString("sggCd");

    public final StringPath sggName = createString("sggName");

    public final NumberPath<Integer> subNumber = createNumber("subNumber", Integer.class);

    public QBuilding(String variable) {
        super(Building.class, forVariable(variable));
    }

    public QBuilding(Path<? extends Building> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBuilding(PathMetadata metadata) {
        super(Building.class, metadata);
    }

}

