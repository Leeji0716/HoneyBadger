package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Cycle;

import java.time.LocalDateTime;
import java.util.List;

public interface CycleRepositoryCustom {

    List<Cycle> myMonthCycle(String k, LocalDateTime startDate, LocalDateTime endDate);

}
