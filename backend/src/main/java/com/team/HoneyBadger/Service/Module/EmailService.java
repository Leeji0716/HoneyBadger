package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;

    public Email save(String title, SiteUser sender) {
        return emailRepository.save(Email.builder().title(title).sender(sender).build());
    }

    public void update(Email email, String content) {
        email.setContent(content);
        emailRepository.save(email);
    }

    public Email getEmail(Long emailId) {
        return emailRepository.findById(emailId).orElseThrow(() -> new RuntimeException("이메일번호가 없습니다."));
    }

    public void findByUsernameDelete(Email email, String username) {
        List<EmailReceiver> emailReceiverList = email.getReceiverList();
        Iterator<EmailReceiver> iterator = emailReceiverList.iterator();

        while (iterator.hasNext()) {
            EmailReceiver emailReceiver = iterator.next();
            if (emailReceiver.getReceiver().getUsername().equals(username)) {
                iterator.remove();
                break;
            }
        }
    }
}