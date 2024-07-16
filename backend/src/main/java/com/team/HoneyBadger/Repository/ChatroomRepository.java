package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.ChatroomRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long>, ChatroomRepositoryCustom {

    Page<Chatroom> findChatroomsByUser(SiteUser user, Pageable pageable);

    Page<Chatroom> findChatroomsByUserAndKeyword(SiteUser user, String keyword, Pageable pageable);

}
