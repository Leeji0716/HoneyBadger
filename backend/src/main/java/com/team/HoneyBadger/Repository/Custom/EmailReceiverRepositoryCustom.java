package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Email;

import java.util.List;

public interface EmailReceiverRepositoryCustom {

    int markEmailAsRead(Long emailId, String receiverId);

    List <Email> findByReceiver(String receiverId);
}