package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.QEmailReservation;
import com.team.HoneyBadger.Repository.Custom.EmailReservationRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailReservationRepositoryImpl implements EmailReservationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QEmailReservation qEmailReservation = QEmailReservation.emailReservation;

    @Override
    public Page<EmailReservation> findReservedEmailsByUserId(String userId, Pageable pageable) {
        BooleanExpression predicate = qEmailReservation.sender.username.eq(userId);

        QueryResults<EmailReservation> queryResults = jpaQueryFactory.selectFrom(qEmailReservation)
                .where(predicate)
                .orderBy(qEmailReservation.sendTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    @Transactional
    public List<EmailReservation> findBySendDate(LocalDateTime nowDate){
        return jpaQueryFactory.select(qEmailReservation).from(qEmailReservation).where(qEmailReservation.sendTime.before(nowDate).and(qEmailReservation.sendTime.isNotNull())).fetch();
    }
}