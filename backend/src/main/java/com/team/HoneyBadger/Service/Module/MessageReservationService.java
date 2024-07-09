package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.DTO.MessageReservationResponseDTO;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.MessageReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReservationService {
    private final MessageReservationRepository messageReservationRepository;


    public MessageReservation save(MessageReservation messageReservation) {
        return messageReservationRepository.save(messageReservation);
    }



    public List<MessageReservation> getMessageReservationFromDate(LocalDateTime nowDate) {
        return messageReservationRepository.findBySendDate(nowDate);
    }

    public void delete(MessageReservation messageReservation) {
        messageReservationRepository.delete(messageReservation);
    }

    public MessageReservation getMessageReservation(Long reservationMessageId) {
        return messageReservationRepository.findById(reservationMessageId).orElseThrow();
    }

    public void update(MessageReservation messageReservation, String message, LocalDateTime sendDate) {
        messageReservation.setMessage(message);
        messageReservation.setSendDate(sendDate);

        messageReservationRepository.save(messageReservation);
    }
}
