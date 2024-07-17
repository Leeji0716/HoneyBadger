package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Repository.Custom.EmailReservationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailReservationRepository extends JpaRepository<EmailReservation, Long>, EmailReservationRepositoryCustom {
    Page<EmailReservation> findReservedEmailsByUserId(String userId, Pageable pageable);

    List<EmailReservation> findBySendDate(LocalDateTime nowDate);
}