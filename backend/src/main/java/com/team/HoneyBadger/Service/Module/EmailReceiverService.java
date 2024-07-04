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
    public void markEmailAsRead(Long emailId, String receiverId) {
        int updatedCount = emailReceiverRepository.markEmailAsRead(emailId, receiverId);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("Invalid email or receiver ID");
        }
    }
    public List<Email> getEmailsForUser(String userId) {
        return emailReceiverRepository.findByReceiver(userId);
    }
}
