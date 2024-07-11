package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.EmailReservationRequestDTO;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Repository.EmailReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailReservationService {
    private final EmailReservationRepository emailReservationRepository;


    public EmailReservation save(EmailReservationRequestDTO reservationRequestDTO, SiteUser sender) {
        return EmailReservation.builder().title(reservationRequestDTO.title()).content(reservationRequestDTO.content()).sender(sender).receiverList(reservationRequestDTO.receiverIds()).sendTime(reservationRequestDTO.sendTime()).build();
    }

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

//    @Transactional
//    public void createEmailFromReservation(EmailReservation emailReservation) {
//        Email email = new Email();
//        email.setTitle(emailReservation.getTitle());
//        email.setContent(emailReservation.getContent());
//        email.setSender(emailReservation.getSender());
//
//        for (String receiverUsername : emailReservation.getReceiverList()) {
//            SiteUser receiver = userRepository.findById(receiverUsername)
//                    .orElseThrow(() -> new RuntimeException("User not found with username: " + receiverUsername));
//            EmailReceiver emailReceiver = new EmailReceiver();
//            emailReceiver.setEmail(email);
//            emailReceiver.setReceiver(receiver);
//            email.getReceiverList().add(emailReceiver);
//        }
//
//        emailRepository.save(email);
//    }
}