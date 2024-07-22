package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QApproval;
import com.team.HoneyBadger.Repository.Custom.ApproverRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApproverRepositoryCustomImpl implements ApproverRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QApproval qApproval  = QApproval.approval;
}
