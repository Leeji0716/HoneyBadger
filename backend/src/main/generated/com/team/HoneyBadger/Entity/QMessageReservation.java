package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMessageReservation is a Querydsl query type for MessageReservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMessageReservation extends EntityPathBase<MessageReservation> {

    private static final long serialVersionUID = 2057123880L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMessageReservation messageReservation = new QMessageReservation("messageReservation");

    public final QChatroom chatroom;

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final EnumPath<com.team.HoneyBadger.Enum.MessageType> messageType = createEnum("messageType", com.team.HoneyBadger.Enum.MessageType.class);

    public final DateTimePath<java.time.LocalDateTime> modifyDate = createDateTime("modifyDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> sendDate = createDateTime("sendDate", java.time.LocalDateTime.class);

    public final QSiteUser sender;

    public QMessageReservation(String variable) {
        this(MessageReservation.class, forVariable(variable), INITS);
    }

    public QMessageReservation(Path<? extends MessageReservation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMessageReservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMessageReservation(PathMetadata metadata, PathInits inits) {
        this(MessageReservation.class, metadata, inits);
    }

    public QMessageReservation(Class<? extends MessageReservation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatroom = inits.isInitialized("chatroom") ? new QChatroom(forProperty("chatroom"), inits.get("chatroom")) : null;
        this.sender = inits.isInitialized("sender") ? new QSiteUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

