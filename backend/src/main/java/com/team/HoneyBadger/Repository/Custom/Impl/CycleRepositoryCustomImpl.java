package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Cycle;
import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Entity.QCycle;
import com.team.HoneyBadger.Repository.Custom.CycleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CycleRepositoryCustomImpl implements CycleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    QCycle qCycle = QCycle.cycle;

    @Override
    public List<Cycle> myMonthCycle(String k, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .selectFrom(qCycle)
                .where(qCycle.k.eq(k).and(qCycle.startDate.between(startDate,endDate).or(qCycle.endDate.between(startDate,endDate))))
                .fetch();
    }

    public List<Cycle> findTagCycle(CycleTag cycleTag){

        return jpaQueryFactory.selectFrom(qCycle).where(qCycle.tag.eq(cycleTag)).fetch();
    }


    public Page<Cycle> findTagCycleToPaging(CycleTag cycleTag , Pageable pageable){

        QueryResults<Cycle> queryResults =   jpaQueryFactory.selectFrom(qCycle)
                .where(qCycle.tag.eq(cycleTag))
                .orderBy(qCycle.startDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

}
