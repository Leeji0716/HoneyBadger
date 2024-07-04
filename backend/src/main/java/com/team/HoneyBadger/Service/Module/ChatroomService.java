package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Repository.ChatroomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomService {
    private final ChatroomRepository chatroomRepository;

    public Chatroom save(String name){
        return this.chatroomRepository.save(Chatroom.builder().name(name).build());
    }

    @Transactional
    public Chatroom getChatRoomById(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElseThrow();
    }

    @Transactional
    public Chatroom modify(String name, Chatroom chatroom) {
        chatroom.setName(name);
        return chatroomRepository.save(chatroom);
    }

    public void delete(Chatroom chatroom) {
        chatroomRepository.delete(chatroom);
    }


}
