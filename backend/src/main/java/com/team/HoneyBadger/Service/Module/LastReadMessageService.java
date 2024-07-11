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
    private final LastReadMessageRepository messageRepository;

    public Optional<LastReadMessage> get(SiteUser user, Chatroom chatroom) {
        return null;
    }
}
