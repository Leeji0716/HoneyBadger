package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Holiday;
import com.team.HoneyBadger.Repository.Custom.HolidayRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
}
