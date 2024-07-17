package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.QParticipant;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.ParticipantRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QParticipant qParticipant = QParticipant.participant;

    public Participant findByUserAndChatroom(SiteUser user, Chatroom chatroom){
        return jpaQueryFactory.selectFrom(qParticipant).where(qParticipant.user.eq(user).and(qParticipant.chatroom.eq(chatroom))).fetchOne();
    }
}
