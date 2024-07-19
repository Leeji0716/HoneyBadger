package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Holiday;

import java.time.LocalDate;

public interface HolidayRepositoryCustom {
    Holiday getHoliday(LocalDate date);
}
