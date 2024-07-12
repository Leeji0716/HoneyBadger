package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getMessageList(Long chatroomId, Long startId);

    List<Message> getList(Long startId);
}
