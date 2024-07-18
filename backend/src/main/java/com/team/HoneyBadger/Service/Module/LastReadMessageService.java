package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.LastReadMessage;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.LastReadMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class LastReadMessageService {
    private final LastReadMessageRepository lastReadMessageRepository;

    public LastReadMessage get(SiteUser user, Chatroom chatroom) {
        return lastReadMessageRepository.getLastMessage(user, chatroom);
    }

    public LastReadMessage create(SiteUser user, Chatroom chatroom, Long lastMessageId) {
        LastReadMessage lastReadMessage = new LastReadMessage();
        lastReadMessage.setSiteUser(user);
        lastReadMessage.setChatroom(chatroom);
        lastReadMessage.setLastReadMessage(lastMessageId);

        return lastReadMessageRepository.save(lastReadMessage);
    }

    public LastReadMessage updateMessage(LastReadMessage lastReadMessage, Long lastReadMessageId) {
        lastReadMessage.setLastReadMessage(lastReadMessageId);
        return lastReadMessageRepository.save(lastReadMessage);
    }
}
