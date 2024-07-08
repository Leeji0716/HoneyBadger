package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
