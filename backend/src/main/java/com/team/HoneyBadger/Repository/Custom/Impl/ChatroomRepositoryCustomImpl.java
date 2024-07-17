package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.QChatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.ChatroomRepository;
import com.team.HoneyBadger.Repository.Custom.ChatroomRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static com.team.HoneyBadger.Entity.QParticipant.participant;

@RequiredArgsConstructor
public class ChatroomRepositoryCustomImpl implements ChatroomRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QChatroom qChatroom = QChatroom.chatroom;

    @Override
    public Page<Chatroom> findChatroomsByUser(SiteUser user, Pageable pageable) {
        List<Chatroom> chatrooms = jpaQueryFactory.selectFrom(qChatroom)
                .join(qChatroom.participants, participant)
                .where(participant.user.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(qChatroom)
                .join(qChatroom.participants, participant)
                .where(participant.user.eq(user))
                .fetchCount();

        return new PageImpl<>(chatrooms, pageable, total);
    }

    @Override
    public Page<Chatroom> findChatroomsByUserAndKeyword(SiteUser user, String keyword, Pageable pageable) {
        List<Chatroom> chatrooms = jpaQueryFactory.selectFrom(qChatroom)
                .join(qChatroom.participants, participant)
                .where(participant.user.eq(user)
                        .and(qChatroom.name.containsIgnoreCase(keyword)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(qChatroom)
                .join(qChatroom.participants, participant)
                .where(participant.user.eq(user)
                        .and(qChatroom.name.containsIgnoreCase(keyword)))
                .fetchCount();

        return new PageImpl<>(chatrooms, pageable, total);
    }
}
