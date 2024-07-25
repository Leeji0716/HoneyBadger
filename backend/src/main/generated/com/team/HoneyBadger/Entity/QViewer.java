package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QViewer is a Querydsl query type for Viewer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QViewer extends EntityPathBase<Viewer> {

    private static final long serialVersionUID = 33682805L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QViewer viewer = new QViewer("viewer");

    public final QApproval approval;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSiteUser user;

    public QViewer(String variable) {
        this(Viewer.class, forVariable(variable), INITS);
    }

    public QViewer(Path<? extends Viewer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QViewer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QViewer(PathMetadata metadata, PathInits inits) {
        this(Viewer.class, metadata, inits);
    }

    public QViewer(Class<? extends Viewer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.approval = inits.isInitialized("approval") ? new QApproval(forProperty("approval"), inits.get("approval")) : null;
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

