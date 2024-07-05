package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.QEmailReservation;
import com.team.HoneyBadger.Repository.Custom.EmailReservationRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailReservationRepositoryImpl implements EmailReservationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QEmailReservation qEmailReservation = QEmailReservation.emailReservation;

    @Override
    @Transactional
    public List<EmailReservation> findBySendTimeBeforeAndSendTimeIsNotNull(LocalDateTime now) {
        return jpaQueryFactory.selectFrom(qEmailReservation)
                .where(qEmailReservation.sendTime.before(now)
                        .and(qEmailReservation.sendTime.isNotNull()))
                .fetch();
    }
}