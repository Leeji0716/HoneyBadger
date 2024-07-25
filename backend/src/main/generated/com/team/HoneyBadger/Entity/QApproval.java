package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApproval is a Querydsl query type for Approval
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApproval extends EntityPathBase<Approval> {

    private static final long serialVersionUID = -2001675034L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApproval approval = new QApproval("approval");

    public final ListPath<Approver, QApprover> approvers = this.<Approver, QApprover>createList("approvers", Approver.class, QApprover.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifyDate = createDateTime("modifyDate", java.time.LocalDateTime.class);

    public final ListPath<String, StringPath> readUsers = this.<String, StringPath>createList("readUsers", String.class, StringPath.class, PathInits.DIRECT2);

    public final QSiteUser sender;

    public final EnumPath<com.team.HoneyBadger.Enum.ApprovalStatus> status = createEnum("status", com.team.HoneyBadger.Enum.ApprovalStatus.class);

    public final StringPath title = createString("title");

    public final ListPath<Viewer, QViewer> viewers = this.<Viewer, QViewer>createList("viewers", Viewer.class, QViewer.class, PathInits.DIRECT2);

    public QApproval(String variable) {
        this(Approval.class, forVariable(variable), INITS);
    }

    public QApproval(Path<? extends Approval> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApproval(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApproval(PathMetadata metadata, PathInits inits) {
        this(Approval.class, metadata, inits);
    }

    public QApproval(Class<? extends Approval> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sender = inits.isInitialized("sender") ? new QSiteUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

