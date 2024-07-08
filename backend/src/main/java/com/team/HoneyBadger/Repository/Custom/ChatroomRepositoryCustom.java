package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.SiteUser;

import java.util.List;

public interface ChatroomRepositoryCustom {
    List<Chatroom> findChatroomsByUser(SiteUser user);
}
