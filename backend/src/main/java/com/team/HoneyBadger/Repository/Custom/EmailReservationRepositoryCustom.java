package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.EmailReservation;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailReservationRepositoryCustom {
    List<EmailReservation> findBySendTimeBeforeAndSendTimeIsNotNull(LocalDateTime now);
}