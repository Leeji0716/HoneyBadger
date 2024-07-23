package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QSiteUser;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QSiteUser qSiteUser = QSiteUser.siteUser;

    @Override
    public List<SiteUser> getUsersDepartmentIsNull() {
        return jpaQueryFactory.selectFrom(qSiteUser).where(qSiteUser.department.isNull()).fetch();
    }

    @Override
    public Page<SiteUser> getUsers(String keyword, Pageable pageable) {
        JPAQuery<SiteUser> query = jpaQueryFactory.selectFrom(qSiteUser);
        if (keyword != null && !keyword.isBlank())
            query = query.where(qSiteUser.name.contains(keyword).or(qSiteUser.department.name.eq(keyword)));
        query = query.orderBy(qSiteUser.name.asc());
        long total = query.fetchCount();
        List<SiteUser> list = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return new PageImpl<>(list, pageable, total);

    }
}
