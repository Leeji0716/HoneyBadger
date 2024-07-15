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
        return emailReservationRepository.save(EmailReservation.builder()
                .title(reservationRequestDTO.title())
                .content(reservationRequestDTO.content())
                .sender(sender)
                .receiverList(reservationRequestDTO.receiverIds())
                .sendTime(reservationRequestDTO.sendTime())
                .build());
    }

    public String saveFile(MultipartFile file) {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            String filePath = path + "/" + file.getOriginalFilename();
            System.out.println("Saving files to: " + filePath);
            file.transferTo(new File(filePath));
            System.out.println("File saved successfully");
            return filePath;
        } catch (IOException e) {
            System.err.println("Failed to save files: " + e.getMessage());
            throw new RuntimeException("Failed to save files", e);
        }
    }

    public EmailReservation getEmailReservation(Long reservationId) {
        return emailReservationRepository.findById(reservationId).orElseThrow();
    }

    public void update(EmailReservation emailReservation, EmailReservationRequestDTO emailReservationRequestDTO) {
        emailReservation.setTitle(emailReservationRequestDTO.title());
        emailReservation.setContent(emailReservationRequestDTO.content());
        emailReservation.setReceiverList(emailReservationRequestDTO.receiverIds());
        emailReservation.setSendTime(emailReservationRequestDTO.sendTime());

        emailReservationRepository.save(emailReservation);
    }

    public List<EmailReservation> getReservedEmailsForUser(String userId) {
        return emailReservationRepository.findReservedEmailsByUserId(userId); // 사용자에 대해 예약된 이메일 목록을 반환하는 로직
    }

    public void delete(EmailReservation emailReservation) {
        emailReservationRepository.delete(emailReservation);
    }
}