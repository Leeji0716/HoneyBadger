package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.MessageReservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageReservationRepositoryCustom {
    List<MessageReservation> findBySendDate(LocalDateTime nowDate);
}
