package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.GroupCycle;
import com.team.HoneyBadger.Repository.Custom.GroupCycleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCycleRepository extends JpaRepository<GroupCycle,Long>, GroupCycleRepositoryCustom {
}
