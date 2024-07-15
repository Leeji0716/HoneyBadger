package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.LastReadMessage;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.LastReadMessageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastReadMessageRepository extends JpaRepository<LastReadMessage, Long>, LastReadMessageRepositoryCustom {

    LastReadMessage getLastMessage(SiteUser user, Chatroom chatroom);
//    LastReadMessage findByUserAndChatroom(SiteUser user, Chatroom chatroom);

}
