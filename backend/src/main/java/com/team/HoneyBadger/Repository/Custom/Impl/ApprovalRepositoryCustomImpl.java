package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.Custom.ApprovalRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.team.HoneyBadger.Entity.QApprover.approver;
import static com.team.HoneyBadger.Entity.QParticipant.participant;
import static com.team.HoneyBadger.Entity.QViewer.viewer;

@RequiredArgsConstructor
public class ApprovalRepositoryCustomImpl implements ApprovalRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QApproval qApproval = QApproval.approval;

    public Page<Approval> findByUsername(String username, Pageable pageable) {
        List<Approval> approvals = jpaQueryFactory
                .selectFrom (qApproval)
                .where (qApproval.approvers.any ().user.username.eq (username)
                        .or (qApproval.viewers.any ().user.username.eq (username))
                        .or (qApproval.sender.username.eq (username)))
                .orderBy (qApproval.createDate.desc ())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch ();

        long total = jpaQueryFactory
                .selectFrom (qApproval)
                .where (qApproval.approvers.any ().user.username.eq (username)
                        .or (qApproval.viewers.any ().user.username.eq (username))
                        .or (qApproval.sender.username.eq (username)))
                .orderBy (qApproval.createDate.desc ())
                .fetchCount ();

        return new PageImpl<> (approvals, pageable, total);

    }

    public Page<Approval> findByUsernameAndKeyword(String username, String keyword, Pageable pageable) {
        List<Approval> approvals = jpaQueryFactory
                .selectFrom (qApproval)
                .where ((qApproval.approvers.any ().user.username.eq (username)
                        .or (qApproval.viewers.any ().user.username.eq (username))
                        .or (qApproval.sender.username.eq (username)))
                        .and (qApproval.title.containsIgnoreCase (keyword)))
                .orderBy (qApproval.createDate.desc ())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch ();

        long total = jpaQueryFactory
                .selectFrom (qApproval)
                .where ((qApproval.approvers.any ().user.username.eq (username)
                        .or (qApproval.viewers.any ().user.username.eq (username))
                        .or (qApproval.sender.username.eq (username)))
                        .and (qApproval.title.containsIgnoreCase (keyword)))
                .orderBy (qApproval.createDate.desc ())
                .fetchCount ();

        return new PageImpl<>(approvals, pageable, total);
    }

}
