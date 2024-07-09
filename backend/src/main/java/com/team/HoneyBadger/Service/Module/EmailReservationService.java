package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Repository.EmailRepository;
import com.team.HoneyBadger.Repository.EmailReservationRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailReservationService {
    private final EmailReservationRepository emailReservationRepository;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    @Transactional
    public void scheduleEmail(String title, String content, SiteUser sender, List<String> receivers, LocalDateTime sendTime, List<MultipartFile> attachments) {
        EmailReservation emailReservation = new EmailReservation();
        emailReservation.setTitle(title);
        emailReservation.setContent(content);
        emailReservation.setSender(sender);
        emailReservation.setSendTime(sendTime);
        emailReservation.setReceiverList(receivers);

        // 첨부 파일 저장
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                System.out.println("Received file: " + file.getOriginalFilename());
                saveFile(file);
            }
        }

        emailReservationRepository.save(emailReservation);
    }

    private void saveFile(MultipartFile file) {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String filePath = path + "/" + file.getOriginalFilename();
            System.out.println("Saving file to: " + filePath);
            file.transferTo(new File(filePath));
            System.out.println("File saved successfully");
        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
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

    @Scheduled(cron = "0 0 */1 * * *")
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