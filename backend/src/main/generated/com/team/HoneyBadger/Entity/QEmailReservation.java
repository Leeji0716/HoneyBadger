package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmailReservation is a Querydsl query type for EmailReservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailReservation extends EntityPathBase<EmailReservation> {

    private static final long serialVersionUID = -390550989L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmailReservation emailReservation = new QEmailReservation("emailReservation");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> receiverList = this.<String, StringPath>createList("receiverList", String.class, StringPath.class, PathInits.DIRECT2);

    public final QSiteUser sender;

    public final DateTimePath<java.time.LocalDateTime> sendTime = createDateTime("sendTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QEmailReservation(String variable) {
        this(EmailReservation.class, forVariable(variable), INITS);
    }

    public QEmailReservation(Path<? extends EmailReservation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmailReservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmailReservation(PathMetadata metadata, PathInits inits) {
        this(EmailReservation.class, metadata, inits);
    }

    public QEmailReservation(Class<? extends EmailReservation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sender = inits.isInitialized("sender") ? new QSiteUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

