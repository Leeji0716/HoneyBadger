package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}