package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.DTO.MessageReservationResponseDTO;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.MessageReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
        return messageReservationRepository.findById(id).orElseThrow( ()-> new DataNotFoundException("없는 예약메세지입니다."));
    }

    public void update(MessageReservation messageReservation, MessageReservationRequestDTO messageReservationRequestDTO) {
        messageReservation.setMessage(messageReservationRequestDTO.message());
        messageReservation.setSendDate(messageReservationRequestDTO.reservationDate());
        messageReservation.setMessageType(messageReservation.getMessageType());
        messageReservation.setModifyDate(LocalDateTime.now());

        messageReservationRepository.save(messageReservation);
    }

    public Page<MessageReservationResponseDTO> getMessageReservationByUser(SiteUser user, Pageable pageable) {
        Page<MessageReservation> messageReservations = messageReservationRepository.getMessageReservationFromUser(user, pageable);

        List<MessageReservationResponseDTO> dtos = messageReservations.getContent().stream()
                .map(this::convertMessageReservationToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, messageReservations.getTotalElements());
    }

    private MessageReservationResponseDTO convertMessageReservationToDTO(MessageReservation messageReservation) {
        return new MessageReservationResponseDTO(
                messageReservation.getId(),
                messageReservation.getChatroom().getId(),
                messageReservation.getMessage(),
                messageReservation.getSender().getUsername(),
                messageReservation.getSender().getName(),
                messageReservation.getSendDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                messageReservation.getMessageType()
        );
    }
}
