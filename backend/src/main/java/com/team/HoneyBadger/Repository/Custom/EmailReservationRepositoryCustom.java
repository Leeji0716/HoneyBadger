package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.EmailReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailReservationRepositoryCustom {
    Page<EmailReservation> findReservedEmailsByUserId(String userId, Pageable pageable);
    List<EmailReservation> findBySendDate(LocalDateTime nowDate);
}