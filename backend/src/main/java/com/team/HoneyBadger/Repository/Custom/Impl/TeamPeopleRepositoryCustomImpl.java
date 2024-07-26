package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QTeamPeople;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.TeamPeople;
import com.team.HoneyBadger.Repository.Custom.TeamPeopleRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TeamPeopleRepositoryCustomImpl implements TeamPeopleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QTeamPeople qTeamPeople = QTeamPeople.teamPeople;

    public List<String> findMyTeamNameList(SiteUser user){

        return jpaQueryFactory.select(qTeamPeople.team.name)
                .from(qTeamPeople)
                .where(qTeamPeople.user.eq(user))
                .fetch();
    }

    public String findMyTeamName(String teamName){


        return jpaQueryFactory.select(qTeamPeople.team.name)
                .from(qTeamPeople)
                .where(qTeamPeople.team.name.eq(teamName))
                .fetchOne();
    }

    public List<TeamPeople> findMyTeam(SiteUser user){

        return jpaQueryFactory
                .selectFrom(qTeamPeople)
                .where(qTeamPeople.user.eq(user))
                .fetch();
    }
}
