package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCycle is a Querydsl query type for Cycle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCycle extends EntityPathBase<Cycle> {

    private static final long serialVersionUID = -1540006621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCycle cycle = new QCycle("cycle");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath k = createString("k");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final QCycleTag tag;

    public final StringPath title = createString("title");

    public QCycle(String variable) {
        this(Cycle.class, forVariable(variable), INITS);
    }

    public QCycle(Path<? extends Cycle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCycle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCycle(PathMetadata metadata, PathInits inits) {
        this(Cycle.class, metadata, inits);
    }

    public QCycle(Class<? extends Cycle> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new QCycleTag(forProperty("tag")) : null;
    }

}

