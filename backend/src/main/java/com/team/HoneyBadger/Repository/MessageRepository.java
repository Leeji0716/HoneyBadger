package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
