package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;

    public Email save(String title, String content, SiteUser sender, LocalDateTime sendTime) {
        return emailRepository.save(Email.builder().title(title).content(content).sender(sender).sendTime(sendTime).build());
    }

    public Email create(SiteUser sender, EmailRequestDTO emailRequestDTO) {
        Email email = Email.builder()
                .title(emailRequestDTO.title())
                .content(emailRequestDTO.content())
                .sender(sender)
                .sendTime(emailRequestDTO.sendTime())
                .build();

        return emailRepository.save(email);
    }

    public Email getEmail(Long emailId) {
        Email email = emailRepository.findById(emailId).orElseThrow(() -> new RuntimeException("Email not found with id: " + emailId));
        return email;
    }

    public void findByUsernameDelete(Email email, String username) {
        List<EmailReceiver> emailReceiverList = email.getReceiverList();
        for (EmailReceiver emailReceiver : emailReceiverList){
            if (emailReceiver.getReceiver().getUsername().equals(username)){
                emailReceiverList.remove(emailReceiver);
                break;
            }
        }
    }
}