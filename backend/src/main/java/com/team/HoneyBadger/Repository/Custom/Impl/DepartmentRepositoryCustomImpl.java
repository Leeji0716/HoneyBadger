package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Department;
import com.team.HoneyBadger.Entity.QDepartment;
import com.team.HoneyBadger.Repository.Custom.DepartmentRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QDepartment qDepartment = QDepartment.department;

    @Override
    public List<Department> getTopList() {
        return jpaQueryFactory.selectFrom(qDepartment).where(qDepartment.parent.isNull()).fetch();
    }
}
