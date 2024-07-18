package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailReceiverRepositoryCustom {

    Boolean markEmailAsRead(Long emailId, String receiverId);

    Page<Email> findSentEmailsByUserId(String userId, Pageable pageable);

    Page<Email> findReceivedEmailsByUserId(String userId, Pageable pageable);

    EmailReceiver findByEmailAndUser(Email email, SiteUser user);
}