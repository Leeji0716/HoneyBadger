package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.LastReadMessage;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.LastReadMessageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class LastReadMessageService {
    private final LastReadMessageRepository lastReadMessageRepository;

    public LastReadMessage get(SiteUser user, Chatroom chatroom) throws NullPointerException {
        return lastReadMessageRepository.getLastMessage(user, chatroom);
//        return null;
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
