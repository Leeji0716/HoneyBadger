package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.PersonalCycle;
import com.team.HoneyBadger.Entity.SiteUser;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalCycleRepositoryCustom {

    List<PersonalCycle> myMonthCycle(SiteUser user, LocalDateTime startDate, LocalDateTime endDate);
}
