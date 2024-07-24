package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Cycle;
import com.team.HoneyBadger.Repository.Custom.CycleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long>, CycleRepositoryCustom {
}
