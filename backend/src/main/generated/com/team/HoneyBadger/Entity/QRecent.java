package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecent is a Querydsl query type for Recent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecent extends EntityPathBase<Recent> {

    private static final long serialVersionUID = -84604482L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecent recent = new QRecent("recent");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSiteUser target;

    public final EnumPath<com.team.HoneyBadger.Enum.RecentType> type = createEnum("type", com.team.HoneyBadger.Enum.RecentType.class);

    public final QSiteUser user;

    public QRecent(String variable) {
        this(Recent.class, forVariable(variable), INITS);
    }

    public QRecent(Path<? extends Recent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecent(PathMetadata metadata, PathInits inits) {
        this(Recent.class, metadata, inits);
    }

    public QRecent(Class<? extends Recent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.target = inits.isInitialized("target") ? new QSiteUser(forProperty("target"), inits.get("target")) : null;
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

