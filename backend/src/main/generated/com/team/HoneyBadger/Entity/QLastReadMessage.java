package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLastReadMessage is a Querydsl query type for LastReadMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLastReadMessage extends EntityPathBase<LastReadMessage> {

    private static final long serialVersionUID = -371556072L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLastReadMessage lastReadMessage1 = new QLastReadMessage("lastReadMessage1");

    public final QChatroom chatroom;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> lastReadMessage = createNumber("lastReadMessage", Long.class);

    public final QSiteUser siteUser;

    public QLastReadMessage(String variable) {
        this(LastReadMessage.class, forVariable(variable), INITS);
    }

    public QLastReadMessage(Path<? extends LastReadMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLastReadMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLastReadMessage(PathMetadata metadata, PathInits inits) {
        this(LastReadMessage.class, metadata, inits);
    }

    public QLastReadMessage(Class<? extends LastReadMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatroom = inits.isInitialized("chatroom") ? new QChatroom(forProperty("chatroom"), inits.get("chatroom")) : null;
        this.siteUser = inits.isInitialized("siteUser") ? new QSiteUser(forProperty("siteUser"), inits.get("siteUser")) : null;
    }

}

