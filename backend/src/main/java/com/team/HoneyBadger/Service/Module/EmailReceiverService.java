package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailReceiverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Email> getEmailsForUser(String userId) {
        return emailReceiverRepository.findByReceiver(userId);
    }
}