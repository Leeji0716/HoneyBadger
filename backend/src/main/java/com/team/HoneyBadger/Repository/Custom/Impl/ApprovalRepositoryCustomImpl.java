package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QApproval;
import com.team.HoneyBadger.Entity.QChatroom;
import com.team.HoneyBadger.Repository.Custom.ApprovalRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApprovalRepositoryCustomImpl implements ApprovalRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QApproval qApproval = QApproval.approval;


}
