package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Repository.MessageReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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

    public MessageReservation getMessageReservation(Long id) {
        return messageReservationRepository.findById(id).orElseThrow();
    }

    public void update(MessageReservation messageReservation, MessageReservationRequestDTO messageReservationRequestDTO) {
        messageReservation.setMessage(messageReservationRequestDTO.message());
        messageReservation.setSendDate(messageReservationRequestDTO.sendDate());
        messageReservation.setMessageType(messageReservation.getMessageType());
        messageReservation.setModifyDate(LocalDateTime.now());

        messageReservationRepository.save(messageReservation);
    }
}
