package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.LastReadMessage;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.LastReadMessageRepository;

import java.util.List;

public interface LastReadMessageRepositoryCustom {
    LastReadMessage getLastMessage(SiteUser user, Chatroom chatroom);
}
