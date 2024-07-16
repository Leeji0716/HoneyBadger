package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.DTO.MessageResponseDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.MessageType;
import com.team.HoneyBadger.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    //나중에 여기서 고쳐 readUsers 나 추가하기
    public Message save(String msg, SiteUser siteUser, Chatroom chatroom, MessageType messageType) {
        Message message = Message.builder().message(msg).sender(siteUser).chatroom(chatroom).messageType(messageType).readUsers(new ArrayList<>()).build();
        return messageRepository.save(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow();
    }

    public List<Message> getList(Long startId) {
        return messageRepository.getList(startId);
    }

    public void updateRead(Message message, List<String> reads) {
        message.setReadUsers(reads);
        messageRepository.save(message);
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    public List<Message> getUpdatedList(Long chatroomId, Long startId) {
        return messageRepository.getMessageList(chatroomId, startId);
    }

    public List<MessageResponseDTO> getMessageList(List<Message> messageList) { //메세지 리스트 메세지 ResponseDTO 변환
        return messageList.stream()
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        message.getMessage(),
                        message.getSender().getUsername(),
                        message.getSender().getName(),
                        message.getCreateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        message.getMessageType().ordinal(),
                        message.getReadUsers() != null ? message.getReadUsers().size() : 0
                ))
                .collect(Collectors.toList());
    }

    public Message getLatesMessage(List<Message> messageList) {
        // 메시지 리스트가 비어 있는지 확인
        if (messageList == null || messageList.isEmpty()) {
            return null; // 또는 예외를 던지거나 적절한 기본 값을 반환
        }

        // 최신 메시지를 찾음
        Message latestMessage = messageList.stream()
                .max(Comparator.comparing(Message::getCreateDate))
                .orElseThrow(() -> new RuntimeException("No messages found"));

        // MessageResponseDTO로 변환 (MessageResponseDTO 생성자 또는 빌더 사용)
        return latestMessage;
    }
}
