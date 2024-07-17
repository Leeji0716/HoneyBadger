package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageReservationRepositoryCustom {
    List<MessageReservation> findBySendDate(LocalDateTime nowDate);

    Page<MessageReservation> getMessageReservationFromUser(SiteUser user, Pageable pageable);
}
