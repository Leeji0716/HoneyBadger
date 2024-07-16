package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailReceiverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Email> getSentEmailsForUser(String userId) {
        return emailReceiverRepository.findSentEmailsByUserId(userId); // 사용자에 대해 보낸 이메일 목록을 반환하는 로직
    }

    public List<Email> getReceivedEmailsForUser(String userId) {
        return emailReceiverRepository.findReceivedEmailsByUserId(userId); // 사용자에 대해 받은 이메일 목록을 반환하는 로직
    }

    public EmailReceiver getReadStatus(Email email, SiteUser user) {
        return emailReceiverRepository.findByEmailAndUser(email, user);
    }

    public Optional<EmailReceiver> getEmailReceiver(Long aLong) {
        return emailReceiverRepository.findById(aLong);
    }
}