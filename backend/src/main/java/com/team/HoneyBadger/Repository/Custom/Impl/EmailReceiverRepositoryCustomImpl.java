package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.QEmailReceiver;
import com.team.HoneyBadger.Repository.Custom.EmailReceiverRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailReceiverRepositoryCustomImpl implements EmailReceiverRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QEmailReceiver qEmailReceiver = QEmailReceiver.emailReceiver;

    @Override
    @Transactional
    public int markEmailAsRead(Long emailId, String receiverId) {
        return (int) jpaQueryFactory.update(qEmailReceiver)
                .set(qEmailReceiver.status, true)
                .where(qEmailReceiver.email.id.eq(emailId)
                        .and(qEmailReceiver.receiver.username.eq(receiverId)))
                .execute();
    }

    @Override
    public List<Email> findByReceiver(String receiverId) {
        return jpaQueryFactory.select(qEmailReceiver.email).from(qEmailReceiver).where(qEmailReceiver.receiver.username.eq(receiverId)).fetch();
    }
}