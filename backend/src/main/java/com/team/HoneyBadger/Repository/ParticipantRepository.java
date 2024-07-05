package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.ParticipantRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepositoryCustom {
    Participant findByUserAndChatroom(SiteUser user, Chatroom chatroom);
}
