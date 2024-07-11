package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.QMessage;
import com.team.HoneyBadger.Repository.Custom.MessageRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QMessage qMessage = QMessage.message1;

    @Override
    public List<Message> getMessageList(Long chatroom_id, Long end_id) {
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.chatroom.id.eq(chatroom_id).and(qMessage.id.gt(end_id).or(qMessage.id.eq(end_id)))).fetch();
    }

    @Override
    public List<Message> getList(Long startId) {
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.id.gt(startId).and(qMessage.id.eq(startId))).fetch();
    }
}
