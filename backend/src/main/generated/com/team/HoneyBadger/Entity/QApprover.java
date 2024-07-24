package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprover is a Querydsl query type for Approver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprover extends EntityPathBase<Approver> {

    private static final long serialVersionUID = -2001674904L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApprover approver = new QApprover("approver");

    public final QApproval approval;

    public final EnumPath<com.team.HoneyBadger.Enum.ApprovalStatus> approverStatus = createEnum("approverStatus", com.team.HoneyBadger.Enum.ApprovalStatus.class);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSiteUser user;

    public QApprover(String variable) {
        this(Approver.class, forVariable(variable), INITS);
    }

    public QApprover(Path<? extends Approver> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApprover(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApprover(PathMetadata metadata, PathInits inits) {
        this(Approver.class, metadata, inits);
    }

    public QApprover(Class<? extends Approver> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.approval = inits.isInitialized("approval") ? new QApproval(forProperty("approval"), inits.get("approval")) : null;
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

