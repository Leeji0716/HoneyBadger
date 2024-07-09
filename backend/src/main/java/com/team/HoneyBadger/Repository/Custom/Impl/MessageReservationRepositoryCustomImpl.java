package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.QMessageReservation;
import com.team.HoneyBadger.Repository.Custom.MessageReservationRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MessageReservationRepositoryCustomImpl implements MessageReservationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QMessageReservation qMessageReservation = QMessageReservation.messageReservation;

    @Override
    @Transactional
    public List<MessageReservation> findBySendDate(LocalDateTime nowDate){
        return jpaQueryFactory.select(qMessageReservation).from(qMessageReservation).where(qMessageReservation.sendDate.before(nowDate).and(qMessageReservation.sendDate.isNotNull())).fetch();
    }


}
