package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Repository.MessageReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReservationService {
    private final MessageReservationRepository messageReservationRepository;


}
