package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.ViewerRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ViewerRepositoryCustomImpl implements ViewerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QViewer qViewer  = QViewer.viewer;

    public Viewer findByUserAndApproval(SiteUser user, Approval approval){
        return jpaQueryFactory.selectFrom(qViewer).where(qViewer.user.eq(user).and(qViewer.approval.eq(approval))).fetchOne ();
    }
    public List<Viewer> findByApproval(Approval approval){
        return jpaQueryFactory.selectFrom(qViewer).where(qViewer.approval.eq(approval)).fetch ();
    }
}
