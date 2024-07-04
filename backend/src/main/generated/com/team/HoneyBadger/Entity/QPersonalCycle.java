package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPersonalCycle is a Querydsl query type for PersonalCycle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPersonalCycle extends EntityPathBase<PersonalCycle> {

    private static final long serialVersionUID = -537878077L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPersonalCycle personalCycle = new QPersonalCycle("personalCycle");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final QSiteUser user;

    public QPersonalCycle(String variable) {
        this(PersonalCycle.class, forVariable(variable), INITS);
    }

    public QPersonalCycle(Path<? extends PersonalCycle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPersonalCycle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPersonalCycle(PathMetadata metadata, PathInits inits) {
        this(PersonalCycle.class, metadata, inits);
    }

    public QPersonalCycle(Class<? extends PersonalCycle> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

