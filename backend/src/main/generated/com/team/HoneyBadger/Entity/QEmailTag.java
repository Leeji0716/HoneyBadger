package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmailTag is a Querydsl query type for EmailTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailTag extends EntityPathBase<EmailTag> {

    private static final long serialVersionUID = 1869184257L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmailTag emailTag = new QEmailTag("emailTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QSiteUser user;

    public QEmailTag(String variable) {
        this(EmailTag.class, forVariable(variable), INITS);
    }

    public QEmailTag(Path<? extends EmailTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmailTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmailTag(PathMetadata metadata, PathInits inits) {
        this(EmailTag.class, metadata, inits);
    }

    public QEmailTag(Class<? extends EmailTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

