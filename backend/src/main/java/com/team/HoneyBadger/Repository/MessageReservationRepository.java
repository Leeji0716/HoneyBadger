package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.MessageReservation;
import com.team.HoneyBadger.Repository.Custom.MessageReservationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReservationRepository extends JpaRepository<MessageReservation, Long>, MessageReservationRepositoryCustom {
}
