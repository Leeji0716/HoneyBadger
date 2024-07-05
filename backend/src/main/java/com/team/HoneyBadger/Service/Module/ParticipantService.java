package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public Participant save(SiteUser user, Chatroom chatroom) {
        return participantRepository.save(Participant.builder().user(user).chatroom(chatroom).build());
    }

    public Participant get(SiteUser user, Chatroom chatroom) {
        return participantRepository.findByUserAndChatroom(user, chatroom);
    }

    public void delete(Participant participant) {
        participantRepository.delete(participant);
    }

    public List<Participant> getAll() {
        return participantRepository.findAll();
    }
}
