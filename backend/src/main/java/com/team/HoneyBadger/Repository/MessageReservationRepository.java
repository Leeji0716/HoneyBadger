package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.MessageReservationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageReservationRepository extends JpaRepository<MessageReservation, Long>, MessageReservationRepositoryCustom {
    List<MessageReservation> findBySendDate(LocalDateTime nowDate);

    Page<MessageReservation> getMessageReservationFromUser(SiteUser user, Pageable pageable);
}
