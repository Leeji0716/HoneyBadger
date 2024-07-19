package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.PersonalCycle;
import com.team.HoneyBadger.Repository.Custom.PersonalCycleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalCycleRepository extends JpaRepository<PersonalCycle, Long>, PersonalCycleRepositoryCustom {
}
