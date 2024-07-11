package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Repository.EmailRepository;
import com.team.HoneyBadger.Repository.EmailReservationRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public String saveFile(MultipartFile file) {
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
            return filePath;
        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Transactional
    public void cancelScheduledEmail(Long emailReservationId) {
        emailReservationRepository.deleteById(emailReservationId);
    }

    public List<EmailReservation> findBySendTimeBeforeAndSendTimeIsNotNull(LocalDateTime now) {
        return emailReservationRepository.findBySendTimeBeforeAndSendTimeIsNotNull(now);
    }

    public void delete(EmailReservation emailReservation) {
        emailReservationRepository.delete(emailReservation);
    }

    public EmailReservation getEmailReservation(Long reservationId) {
        return emailReservationRepository.findById(reservationId).orElseThrow();
    }

    public void findByUsernameDelete(EmailReservation emailReservation, String username) {
        List<String> emailReceiverList = emailReservation.getReceiverList();
        for (String emailReceiver : emailReceiverList)
            if (emailReceiver.equals(username)) {
                emailReceiverList.remove(emailReceiver);
                break;
            }
    }
}