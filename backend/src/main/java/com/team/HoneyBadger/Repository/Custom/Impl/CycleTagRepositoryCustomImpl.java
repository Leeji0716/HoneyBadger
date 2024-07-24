package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Entity.QCycleTag;
import com.team.HoneyBadger.Repository.Custom.CycleTagRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CycleTagRepositoryCustomImpl implements CycleTagRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QCycleTag qCycleTag = QCycleTag.cycleTag;

    public CycleTag findByName(String k,String name){
        return jpaQueryFactory.selectFrom(qCycleTag).where(qCycleTag.k.eq(k).and(qCycleTag.name.eq(name))).fetchOne();
    }
}
