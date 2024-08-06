package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.ChatroomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomService {
    private final ChatroomRepository chatroomRepository;

    @Transactional
    public Chatroom save(Chatroom chatroom) {
        return chatroomRepository.save(chatroom);
    }

    @Transactional
    public Chatroom create(String name) {
        return chatroomRepository.save(Chatroom.builder().name(name).build());
    }

    @Transactional
    public Chatroom getChatRoomById(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElseThrow(()-> new DataNotFoundException("없는 채팅방입니다."));
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
    public Page<Chatroom> getChatRoomListByUser(SiteUser user, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            // 키워드가 없을 경우, 기본적으로 유저의 모든 채팅방을 반환
            return chatroomRepository.findChatroomsByUser(user, pageable);

        } else {
            // 키워드가 있을 경우, 키워드를 포함한 채팅방을 반환
            return chatroomRepository.findChatroomsByUserAndKeyword(user, keyword, pageable);
        }
    }

    public void notification(Chatroom chatroom, Message message) {
        chatroom.setNotification(message);
        chatroomRepository.save(chatroom);
    }
}
