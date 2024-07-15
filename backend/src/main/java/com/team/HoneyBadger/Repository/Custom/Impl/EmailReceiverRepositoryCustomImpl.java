package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.EmailReceiverRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailReceiverRepositoryCustomImpl implements EmailReceiverRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QEmail qEmail = QEmail.email;
    QEmailReceiver qEmailReceiver = QEmailReceiver.emailReceiver;
    QEmailReservation qEmailReservation = QEmailReservation.emailReservation;

    @Override
    public List<Email> findByReceiver(String receiverId) {
        return jpaQueryFactory.select(qEmailReceiver.email).from(qEmailReceiver).where(qEmailReceiver.receiver.username.eq(receiverId)).fetch();
    }

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
    public List<Email> findSentEmailsByUserId(String userId) {
        return jpaQueryFactory.selectFrom(qEmail)
                .where(qEmail.sender.username.eq(userId))
                .fetch();
    }

    @Override
    public List<Email> findReceivedEmailsByUserId(String userId) {
        return jpaQueryFactory.selectFrom(qEmail)
                .join(qEmail.receiverList, qEmailReceiver)
                .where(qEmailReceiver.receiver.username.eq(userId))
                .fetch();
    }

    public EmailReceiver findByEmailAndUser(Email email, SiteUser user) {
        return jpaQueryFactory.selectFrom(qEmailReceiver).where(qEmailReceiver.receiver.eq(user).and(qEmailReceiver.email.eq(email))).fetchOne();
    }

}