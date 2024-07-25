package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.ApproverRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApproverRepositoryCustomImpl implements ApproverRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QApprover qApprover  = QApprover.approver;

    public Approver findByUserAndApproval(SiteUser user, Approval approval){
        return jpaQueryFactory.selectFrom(qApprover).where(qApprover.user.eq(user).and(qApprover.approval.eq(approval))).fetchOne ();
    }

    public Approver findByUsernameAndApproval(String username, Approval approval){
        return jpaQueryFactory.selectFrom(qApprover).where(qApprover.user.username.eq(username).and(qApprover.approval.eq(approval))).fetchOne ();
    }
    public List<Approver> findByApproval(Approval approval){
        return jpaQueryFactory.selectFrom(qApprover).where(qApprover.approval.eq(approval)).fetch ();
    }

}
