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
    public List<Message> getMessageList(Long chatroomId, Long startId) {
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.chatroom.id.eq(chatroomId).and(qMessage.id.gt(startId).or(qMessage.id.eq(startId)))).fetch();
    }

    @Override
    public List<Message> getList(Long startId) {
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.id.gt(startId).and(qMessage.id.eq(startId))).fetch();
    }
}
