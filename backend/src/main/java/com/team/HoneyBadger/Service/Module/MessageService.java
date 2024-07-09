package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageResponseDTO;
import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow();
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    public List<MessageResponseDTO> getMessageList(List<Message> messageList) { //메세지 리스트 메세지리스폰스DTO로 변환
        return messageList.stream()
                .sorted((m1, m2) -> m2.getCreateDate().compareTo(m1.getCreateDate()))
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        message.getMessage(),
                        message.getSender().getName(),
                        message.getCreateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ))
                .collect(Collectors.toList());
    }

}
