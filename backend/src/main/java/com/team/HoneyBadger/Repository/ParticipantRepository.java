package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Participant findByUserAndChatroom(SiteUser user, Chatroom chatroom);
}
