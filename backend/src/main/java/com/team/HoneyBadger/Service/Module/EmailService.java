package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private EmailRepository emailRepository;

    public Email save(String title, String content, SiteUser sender) {
        return emailRepository.save(Email.builder().title(title).content(content).sender(sender).build());
    }
}