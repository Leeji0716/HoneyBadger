package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmail is a Querydsl query type for Email
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmail extends EntityPathBase<Email> {

    private static final long serialVersionUID = -1538519079L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmail email = new QEmail("email");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<EmailReceiver, QEmailReceiver> receiverList = this.<EmailReceiver, QEmailReceiver>createList("receiverList", EmailReceiver.class, QEmailReceiver.class, PathInits.DIRECT2);

    public final QSiteUser sender;

    public final QEmailTag tag;

    public final StringPath title = createString("title");

    public QEmail(String variable) {
        this(Email.class, forVariable(variable), INITS);
    }

    public QEmail(Path<? extends Email> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmail(PathMetadata metadata, PathInits inits) {
        this(Email.class, metadata, inits);
    }

    public QEmail(Class<? extends Email> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sender = inits.isInitialized("sender") ? new QSiteUser(forProperty("sender"), inits.get("sender")) : null;
        this.tag = inits.isInitialized("tag") ? new QEmailTag(forProperty("tag"), inits.get("tag")) : null;
    }

}

