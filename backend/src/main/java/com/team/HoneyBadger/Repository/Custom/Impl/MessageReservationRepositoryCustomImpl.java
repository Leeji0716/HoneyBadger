package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.QMessageReservation;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.MessageReservationRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.team.HoneyBadger.Entity.QParticipant.participant;

@RequiredArgsConstructor
public class MessageReservationRepositoryCustomImpl implements MessageReservationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QMessageReservation qMessageReservation = QMessageReservation.messageReservation;

    @Override
    @Transactional
    public List<MessageReservation> findBySendDate(LocalDateTime nowDate){
        return jpaQueryFactory.select(qMessageReservation).from(qMessageReservation).where(qMessageReservation.sendDate.before(nowDate).and(qMessageReservation.sendDate.isNotNull())).fetch();
    }

    @Override
    public Page<MessageReservation> getMessageReservationFromUser(SiteUser user, Pageable pageable){
        List<MessageReservation> messageReservations = jpaQueryFactory.selectFrom(qMessageReservation)
                .where(qMessageReservation.sender.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(qMessageReservation)
                .where(qMessageReservation.sender.eq(user))
                .fetchCount();

        return new PageImpl<>(messageReservations, pageable, total);
    }


}
