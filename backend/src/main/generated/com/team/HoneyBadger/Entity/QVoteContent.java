package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteContent is a Querydsl query type for VoteContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteContent extends EntityPathBase<VoteContent> {

    private static final long serialVersionUID = 135143596L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteContent voteContent = new QVoteContent("voteContent");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QVote vote;

    public final SetPath<String, StringPath> voters = this.<String, StringPath>createSet("voters", String.class, StringPath.class, PathInits.DIRECT2);

    public QVoteContent(String variable) {
        this(VoteContent.class, forVariable(variable), INITS);
    }

    public QVoteContent(Path<? extends VoteContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteContent(PathMetadata metadata, PathInits inits) {
        this(VoteContent.class, metadata, inits);
    }

    public QVoteContent(Class<? extends VoteContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vote = inits.isInitialized("vote") ? new QVote(forProperty("vote"), inits.get("vote")) : null;
    }

}

