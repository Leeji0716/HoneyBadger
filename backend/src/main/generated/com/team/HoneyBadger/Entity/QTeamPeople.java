package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamPeople is a Querydsl query type for TeamPeople
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamPeople extends EntityPathBase<TeamPeople> {

    private static final long serialVersionUID = -1913667281L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamPeople teamPeople = new QTeamPeople("teamPeople");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.team.HoneyBadger.Enum.TeamRole> role = createEnum("role", com.team.HoneyBadger.Enum.TeamRole.class);

    public final QTeam team;

    public final QSiteUser user;

    public QTeamPeople(String variable) {
        this(TeamPeople.class, forVariable(variable), INITS);
    }

    public QTeamPeople(Path<? extends TeamPeople> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamPeople(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamPeople(PathMetadata metadata, PathInits inits) {
        this(TeamPeople.class, metadata, inits);
    }

    public QTeamPeople(Class<? extends TeamPeople> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team")) : null;
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

