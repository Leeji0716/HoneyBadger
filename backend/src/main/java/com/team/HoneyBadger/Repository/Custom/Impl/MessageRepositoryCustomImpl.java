package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.QMessage;
import com.team.HoneyBadger.Repository.Custom.MessageRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        return jpaQueryFactory.selectFrom(qMessage).where(qMessage.id.gt(startId).or(qMessage.id.eq(startId))).fetch();
    }

    @Override
    public Page<Message> findAllPage(Pageable pageable) {
        QueryResults<Message> results = jpaQueryFactory
                .selectFrom(qMessage)
                .orderBy(qMessage.createDate.desc())
                .offset(pageable.getOffset()) // 페이지 시작 인덱스
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
