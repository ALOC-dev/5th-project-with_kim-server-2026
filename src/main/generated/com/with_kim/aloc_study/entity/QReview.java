package com.with_kim.aloc_study.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -934519447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final NumberPath<Integer> cleanlinessRating = createNumber("cleanlinessRating", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QHouse house;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image_url1 = createString("image_url1");

    public final StringPath image_url2 = createString("image_url2");

    public final StringPath image_url3 = createString("image_url3");

    public final NumberPath<Integer> locationRating = createNumber("locationRating", Integer.class);

    public final NumberPath<Integer> managementRating = createNumber("managementRating", Integer.class);

    public final NumberPath<Integer> priceRating = createNumber("priceRating", Integer.class);

    public final StringPath text = createString("text");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUsers user;

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.house = inits.isInitialized("house") ? new QHouse(forProperty("house"), inits.get("house")) : null;
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user")) : null;
    }

}

