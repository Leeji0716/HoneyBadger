package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.MessageReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReservationRepository extends JpaRepository<MessageReservation, Long> {
}
