package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getMessageList(Long chatroomId, Long startId);

    List<Message> getList(Long startId);

    List<Message> findImageMessagesByChatroom(Chatroom chatroom);

    List<Message> findLinkMessagesByChatroom(Chatroom chatroom);

    List<Message> findFileMessagesByChatroom(Chatroom chatroom);
}
