package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.EmailReservationRequestDTO;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.EmailReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailReservationService {
    private final EmailReservationRepository emailReservationRepository;


    public EmailReservation save(String title, List<String> receiverIds, SiteUser sender, LocalDateTime sendTime) {
        return emailReservationRepository.save(EmailReservation.builder()
                .title(title)
                .sender(sender)
                .receiverList(receiverIds)
                .sendTime(sendTime)
                .build());
    }

    public void update(EmailReservation reservation, String content) {
        reservation.setContent(content);
        emailReservationRepository.save(reservation);
    }

    public EmailReservation getEmailReservation(Long reservationId) {
        return emailReservationRepository.findById(reservationId).orElseThrow(() -> new DataNotFoundException("없는 예약 메일 입니다."));
    }

    public void update(EmailReservation emailReservation, EmailReservationRequestDTO emailReservationRequestDTO) {
        emailReservation.setTitle(emailReservationRequestDTO.title());
        emailReservation.setContent(emailReservationRequestDTO.content());
        emailReservation.setReceiverList(emailReservationRequestDTO.receiverIds());
        emailReservation.setSendTime(emailReservationRequestDTO.sendTime());

        emailReservationRepository.save(emailReservation);
    }

    public Page<EmailReservation> getReservedEmailsForUser(String userId, Pageable pageable) {
        return emailReservationRepository.findReservedEmailsByUserId(userId, pageable); // 사용자에 대해 예약된 이메일 목록을 반환하는 로직
    }

    public void delete(EmailReservation emailReservation) {
        emailReservationRepository.delete(emailReservation);
    }

    public List<EmailReservation> getEmailReservationFromDate(LocalDateTime nowDate) {
        return emailReservationRepository.findBySendDate(nowDate);
    }
}