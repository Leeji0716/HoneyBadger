package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.QChatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.ChatroomRepository;
import com.team.HoneyBadger.Repository.Custom.ChatroomRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static com.team.HoneyBadger.Entity.QParticipant.participant;

@RequiredArgsConstructor
public class ChatroomRepositoryCustomImpl implements ChatroomRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QChatroom qChatroom = QChatroom.chatroom;

    public List<Chatroom> findChatroomsByUser(SiteUser user){
        return jpaQueryFactory.select(qChatroom).from(qChatroom).join(qChatroom.participants, participant).where(participant.user.eq(user)).fetch();
    }
}
