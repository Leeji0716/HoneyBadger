package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QGroupCycle;
import com.team.HoneyBadger.Repository.Custom.GroupCycleRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupCycleRepositoryCustomImpl implements GroupCycleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    QGroupCycle qGroupCycle = QGroupCycle.groupCycle;
}
