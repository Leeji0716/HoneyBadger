package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.QMessage;
import com.team.HoneyBadger.Enum.MessageType;
import com.team.HoneyBadger.Repository.Custom.MessageRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.id.gt(startId).or(qMessage.id.eq(startId))).fetch(); //list fetchOne
    }

    @Override
    public List<Message> findImageMessagesByChatroom(Chatroom chatroom){
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.chatroom.eq(chatroom).and(qMessage.messageType.eq(MessageType.IMAGE))).fetch();
    }

    @Override
    public List<Message> findLinkMessagesByChatroom(Chatroom chatroom){
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.chatroom.eq(chatroom).and(qMessage.messageType.eq(MessageType.LINK))).fetch();
    }

    @Override
    public List<Message> findFileMessagesByChatroom(Chatroom chatroom){
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.chatroom.eq(chatroom).and(qMessage.messageType.eq(MessageType.FILE))).fetch();
    }
}
