package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;

import java.util.List;

public interface ParticipantRepositoryCustom {
    Participant findByUserAndChatroom(SiteUser user, Chatroom chatroom);
}
