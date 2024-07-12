package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGroupCycle is a Querydsl query type for GroupCycle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroupCycle extends EntityPathBase<GroupCycle> {

    private static final long serialVersionUID = 938558346L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGroupCycle groupCycle = new QGroupCycle("groupCycle");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final QDepartment group;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QGroupCycle(String variable) {
        this(GroupCycle.class, forVariable(variable), INITS);
    }

    public QGroupCycle(Path<? extends GroupCycle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGroupCycle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGroupCycle(PathMetadata metadata, PathInits inits) {
        this(GroupCycle.class, metadata, inits);
    }

    public QGroupCycle(Class<? extends GroupCycle> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new QDepartment(forProperty("group"), inits.get("group")) : null;
    }

}

