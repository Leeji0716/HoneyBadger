package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.PersonalCycle;
import com.team.HoneyBadger.Entity.QPersonalCycle;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.PersonalCycleRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PersonalCycleRepositoryCustomImpl implements PersonalCycleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    QPersonalCycle qPersonalCycle = QPersonalCycle.personalCycle;

    @Override
    public List<PersonalCycle> myMonthCycle(SiteUser user, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .selectFrom(qPersonalCycle)
                .where(qPersonalCycle.startDate.between(startDate,endDate).or(qPersonalCycle.endDate.between(startDate,endDate).or(qPersonalCycle.endDate.after(endDate))))
                .fetch();
    }


    public List<PersonalCycle> tagList(SiteUser user,String tag){


        return jpaQueryFactory.selectFrom(qPersonalCycle).where(qPersonalCycle.tag.contains(tag).and(qPersonalCycle.user.eq(user))).fetch();


    }




}
