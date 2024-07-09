package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Repository.Custom.MessageReservationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageReservationRepository extends JpaRepository<MessageReservation, Long>, MessageReservationRepositoryCustom {
    List<MessageReservation> findBySendDate(LocalDateTime nowDate);
}
