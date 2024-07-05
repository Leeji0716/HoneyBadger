package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;

import java.util.List;

public interface ParticipantRepositoryCustom {
    List<Participant> findByUserIn(List<SiteUser> users);
}
