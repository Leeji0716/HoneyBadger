package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.QParticipant;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.ParticipantRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QParticipant qParticipant = QParticipant.participant;

    public List<Participant> findByUserIn(List<SiteUser> users){
        return jpaQueryFactory.select(qParticipant).from(qParticipant).where(qParticipant.user.in(users)).fetch();
    }

}
