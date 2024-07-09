package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailRepository;
import com.team.HoneyBadger.Repository.EmailReservationRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailReservationService {

    private final EmailReservationRepository emailReservationRepository;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    @Transactional
    public void scheduleEmail(String title, String content, SiteUser sender, List<String> receivers, LocalDateTime sendTime) {
        EmailReservation emailReservation = new EmailReservation();
        emailReservation.setTitle(title);
        emailReservation.setContent(content);
        emailReservation.setSender(sender);
        emailReservation.setSendTime(sendTime);
        emailReservation.setReceiverList(receivers);

        emailReservationRepository.save(emailReservation);
    }

    @Transactional
    public void cancelScheduledEmail(Long emailReservationId) {
        emailReservationRepository.deleteById(emailReservationId);
    }

    @Transactional
    public void createEmailFromReservation(EmailReservation emailReservation) {
        Email email = new Email();
        email.setTitle(emailReservation.getTitle());
        email.setContent(emailReservation.getContent());
        email.setSender(emailReservation.getSender());

        for (String receiverUsername : emailReservation.getReceiverList()) {
            SiteUser receiver = userRepository.findById(receiverUsername)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + receiverUsername));
            EmailReceiver emailReceiver = new EmailReceiver();
            emailReceiver.setEmail(email);
            emailReceiver.setReceiver(receiver);
            email.getReceiverList().add(emailReceiver);
        }

        emailRepository.save(email);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void processScheduledEmails() {
        LocalDateTime now = LocalDateTime.now();
        List<EmailReservation> emailsToProcess = emailReservationRepository.findBySendTimeBeforeAndSendTimeIsNotNull(now);

        for (EmailReservation emailReservation : emailsToProcess) {
            createEmailFromReservation(emailReservation);
            emailReservationRepository.delete(emailReservation);
        }
    }

    public SiteUser getUserByUsername(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}