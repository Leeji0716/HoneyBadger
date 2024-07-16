package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Repository.Custom.EmailReservationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailReservationRepository extends JpaRepository<EmailReservation, Long>, EmailReservationRepositoryCustom {
    Page<EmailReservation> findReservedEmailsByUserId(String userId, Pageable pageable);
}