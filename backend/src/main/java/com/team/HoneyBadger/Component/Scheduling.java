package com.team.HoneyBadger.Component;

import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Service.Module.MessageReservationService;
import com.team.HoneyBadger.Service.MultiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class Scheduling {
    private final MultiService multiService;
    private final MessageReservationService messageReservationService;

    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void sendReservation() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + LocalDateTime.now() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        List<MessageReservation> messageReservationList = messageReservationService.getMessageReservationFromDate(LocalDateTime.now());
        for (MessageReservation messageReservation : messageReservationList) {
            if (messageReservation.getSendDate().toLocalTime().isBefore(LocalTime.now())) {
                MessageRequestDTO messageRequestDTO = new MessageRequestDTO(messageReservation.getMessage(), messageReservation.getSender().getUsername(), messageReservation.getMessageType());
                multiService.sendMessage(messageReservation.getId(), messageRequestDTO);
                messageReservationService.delete(messageReservation);
            }
        }
    }
}
