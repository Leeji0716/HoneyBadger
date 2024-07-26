package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.TeamPeople;
import com.team.HoneyBadger.Repository.Custom.TeamPeopleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPeopleRepository extends JpaRepository<TeamPeople,Long>, TeamPeopleRepositoryCustom {
}
