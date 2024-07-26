package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.TeamPeople;

import java.util.List;

public interface TeamPeopleRepositoryCustom {

    List<String> findMyTeamNameList(SiteUser user);

    String findMyTeamName(String teamName);

    List<TeamPeople> findMyTeam(SiteUser user);
}
