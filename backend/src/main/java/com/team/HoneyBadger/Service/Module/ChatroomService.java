package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.ChatroomRequestDTO;
import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.ChatroomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatroomService {
    private final ChatroomRepository chatroomRepository;

    @Transactional
    public Chatroom save(Chatroom chatroom) {
        return this.chatroomRepository.save(chatroom);
    }

    @Transactional
    public Chatroom create(String name) {
        return chatroomRepository.save(Chatroom.builder().name(name).build());
    }

    @Transactional
    public Chatroom getChatRoomById(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElseThrow();
    }

    @Transactional
    public void delete(Chatroom chatroom) {
        chatroomRepository.delete(chatroom);
    }

    @Transactional
    public Chatroom updateChatroom(Chatroom chatroom, String name) {
        chatroom.setName(name);
        return chatroomRepository.save(chatroom);
    }

    @Transactional
    public List<Chatroom> getChatRoomListByUser(SiteUser user) {
        return chatroomRepository.findChatroomsByUser(user);
    }
}
