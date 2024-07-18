package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Repository.Custom.EmailReceiverRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Long>, EmailReceiverRepositoryCustom {
}