package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Repository.Custom.MessageRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {
    public Page<Message> findAllPage(Pageable pageable);
}
