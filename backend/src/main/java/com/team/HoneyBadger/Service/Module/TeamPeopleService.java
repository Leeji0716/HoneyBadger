package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.TeamPeople;
import com.team.HoneyBadger.Repository.TeamPeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamPeopleService {
    private final TeamPeopleRepository teamPeopleRepository;

    public List<String> findMyTeamNameList(SiteUser user){
        return teamPeopleRepository.findMyTeamNameList(user);
    }

    public String findMyTeamName(String teamName){
        return teamPeopleRepository.findMyTeamName(teamName);
    }

    public List<TeamPeople> findMyTeam(SiteUser user){
        return teamPeopleRepository.findMyTeam(user);
    }
}
