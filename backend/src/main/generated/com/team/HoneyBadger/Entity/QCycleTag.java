package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCycleTag is a Querydsl query type for CycleTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCycleTag extends EntityPathBase<CycleTag> {

    private static final long serialVersionUID = 503493495L;

    public static final QCycleTag cycleTag = new QCycleTag("cycleTag");

    public final StringPath color = createString("color");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath k = createString("k");

    public final StringPath name = createString("name");

    public QCycleTag(String variable) {
        super(CycleTag.class, forVariable(variable));
    }

    public QCycleTag(Path<? extends CycleTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCycleTag(PathMetadata metadata) {
        super(CycleTag.class, metadata);
    }

}

