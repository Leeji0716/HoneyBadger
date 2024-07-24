package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Repository.Custom.CycleTagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleTagRepository extends JpaRepository<CycleTag,Long>, CycleTagRepositoryCustom {
}
