package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.EmailReceiverRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Long>, EmailReceiverRepositoryCustom {

    EmailReceiver findByEmailAndUser(Email email, SiteUser user);
}