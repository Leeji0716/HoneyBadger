package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.EmailReceiverRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailReceiverRepositoryCustomImpl implements EmailReceiverRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QEmail qEmail = QEmail.email;
    QEmailReceiver qEmailReceiver = QEmailReceiver.emailReceiver;

    @Override
    @Transactional
    public Boolean markEmailAsRead(Long emailId, String receiverId) {
        long updatedRows = jpaQueryFactory.update(qEmailReceiver)
                .set(qEmailReceiver.status, true)
                .where(qEmailReceiver.email.id.eq(emailId)
                        .and(qEmailReceiver.receiver.username.eq(receiverId)))
                .execute();
        return updatedRows > 0;
    }

    @Override
    public Page<Email> findSentEmailsByUserId(String userId, Pageable pageable) {
        QueryResults<Email> queryResults = jpaQueryFactory.selectFrom(qEmail)
                .where(qEmail.sender.username.eq(userId))
                .orderBy(qEmail.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }


    @Override
    public Page<Email> findReceivedEmailsByUserId(String userId, Pageable pageable) {
        QueryResults<Email> queryResults = jpaQueryFactory.selectFrom(qEmail)
                .join(qEmail.receiverList, qEmailReceiver)
                .where(qEmailReceiver.receiver.username.eq(userId))
                .orderBy(qEmail.createDate.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    public EmailReceiver findByEmailAndUser(Email email, SiteUser user) {
        return jpaQueryFactory.selectFrom(qEmailReceiver)
                .where(qEmailReceiver.receiver.eq(user)
                        .and(qEmailReceiver.email.eq(email)))
                .fetchOne();
    }
}