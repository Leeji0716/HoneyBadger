package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.LastReadMessage;
import com.team.HoneyBadger.Entity.QLastReadMessage;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.LastReadMessageRepositoryCustom;
import com.team.HoneyBadger.Repository.LastReadMessageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LastReadMessageRepositoryCustomImpl implements LastReadMessageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QLastReadMessage qLastReadMessage = QLastReadMessage.lastReadMessage1;

    @Override
    public LastReadMessage getLastMessage(SiteUser user, Chatroom chatroom) {
        return jpaQueryFactory.selectFrom(qLastReadMessage).where(qLastReadMessage.siteUser.eq(user).and(qLastReadMessage.chatroom.eq(chatroom))).fetchOne();
    }
}
