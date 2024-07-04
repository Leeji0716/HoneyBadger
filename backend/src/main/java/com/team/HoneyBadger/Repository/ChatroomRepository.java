package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}
