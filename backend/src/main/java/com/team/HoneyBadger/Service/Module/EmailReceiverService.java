package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailReceiverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailReceiverService {
    private final EmailReceiverRepository emailReceiverRepository;

    public void save(Email email, SiteUser receiver) {
        emailReceiverRepository.save(EmailReceiver.builder().email(email).receiver(receiver).build());
    }

    @Transactional
    public Boolean markEmailAsRead(Long emailId, String receiverId) {
        Boolean isRead = emailReceiverRepository.markEmailAsRead(emailId, receiverId);
        if (!isRead) {
            throw new IllegalArgumentException("Invalid email or receiver ID");
        }
        return isRead;
    }

    public Page<Email> getSentEmailsForUser(String userId, Pageable pageable) {
        return emailReceiverRepository.findSentEmailsByUserId(userId, pageable); // 사용자에 대해 보낸 이메일 목록을 반환하는 로직
    }

    public Page<Email> getReceivedEmailsForUser(String userId, Pageable pageable) {
        return emailReceiverRepository.findReceivedEmailsByUserId(userId, pageable); // 사용자에 대해 받은 이메일 목록을 반환하는 로직
    }

    public EmailReceiver getReadStatus(Email email, SiteUser user) {
        return emailReceiverRepository.findByEmailAndUser(email, user);
    }

    public Optional<EmailReceiver> getEmailReceiver(Long aLong) {
        return emailReceiverRepository.findById(aLong);
    }
}