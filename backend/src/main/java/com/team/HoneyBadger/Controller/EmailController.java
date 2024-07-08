package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.EmailResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final MultiService multiService;

    @PostMapping
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequestDTO emailDTO) {
        Email email = multiService.sendEmail(emailDTO.title(), emailDTO.content(), emailDTO.senderId(), emailDTO.receiverIds(), emailDTO.sendTime());
        return ResponseEntity.ok(email);
    }

    @GetMapping
    public ResponseEntity<?> getEmailsForUser(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            List<EmailResponseDTO> emails = multiService.getEmailsForUser(tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(emails);
        } else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/read")
    public ResponseEntity<?> markEmailAsRead(@RequestBody Long emailId, @RequestBody String receiverId) {
        multiService.markEmailAsRead(emailId, receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}