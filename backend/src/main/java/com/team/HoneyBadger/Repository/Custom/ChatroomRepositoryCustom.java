package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatroomRepositoryCustom {
    Page<Chatroom> findChatroomsByUser(SiteUser user, Pageable pageable);

    Page<Chatroom> findChatroomsByUserAndKeyword(SiteUser user, String keyword, Pageable pageable);

}
