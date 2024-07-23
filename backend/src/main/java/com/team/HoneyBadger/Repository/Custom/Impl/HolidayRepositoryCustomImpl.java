package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.Holiday;
import com.team.HoneyBadger.Entity.QHoliday;
import com.team.HoneyBadger.Repository.Custom.HolidayRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class HolidayRepositoryCustomImpl implements HolidayRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QHoliday qHoliday = QHoliday.holiday;


    public Holiday getHoliday(LocalDate date){

        return jpaQueryFactory.selectFrom(qHoliday).where(qHoliday.nowDate.eq(date)).fetchOne();
    }
}
