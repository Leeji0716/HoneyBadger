package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmailReceiver is a Querydsl query type for EmailReceiver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailReceiver extends EntityPathBase<EmailReceiver> {

    private static final long serialVersionUID = 477459688L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmailReceiver emailReceiver = new QEmailReceiver("emailReceiver");

    public final QEmail email;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSiteUser receiver;

    public final BooleanPath status = createBoolean("status");

    public QEmailReceiver(String variable) {
        this(EmailReceiver.class, forVariable(variable), INITS);
    }

    public QEmailReceiver(Path<? extends EmailReceiver> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmailReceiver(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmailReceiver(PathMetadata metadata, PathInits inits) {
        this(EmailReceiver.class, metadata, inits);
    }

    public QEmailReceiver(Class<? extends EmailReceiver> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.email = inits.isInitialized("email") ? new QEmail(forProperty("email"), inits.get("email")) : null;
        this.receiver = inits.isInitialized("receiver") ? new QSiteUser(forProperty("receiver"), inits.get("receiver")) : null;
    }

}

