package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Repository.Custom.MessageRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {

    List<Message> getMessageList(Long chatroomId, Long startId);

    List<Message> getList(Long startId);

    List<Message> findImageMessagesByChatroom(Chatroom chatroom);

    List<Message> findLinkMessagesByChatroom(Chatroom chatroom);

    List<Message> findFileMessagesByChatroom(Chatroom chatroom);
}
