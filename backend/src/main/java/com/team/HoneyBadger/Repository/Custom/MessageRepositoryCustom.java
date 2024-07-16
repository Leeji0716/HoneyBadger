package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getMessageList(Long chatroomId, Long startId);

    List<Message> getList(Long startId);

    public Page<Message> findAllPage(Pageable pageable);
}
