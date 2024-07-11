package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.Module.EmailReservationService;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final MultiService multiService;
    private final EmailReservationService emailReservationService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendEmail(
            @ModelAttribute EmailRequestDTO emailDTO,
            @RequestParam("attachments") List<MultipartFile> attachments
    ) {
        System.out.println("Received email send request");
        SiteUser sender = multiService.getUserByUsername(emailDTO.senderId());
        multiService.scheduleEmail(
                emailDTO.title(),
                emailDTO.content(),
                sender,
                emailDTO.receiverIds(),
                emailDTO.sendTime(),
                attachments
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 선택해주세요.");
        if (tokenDTO.isOK()) try {
            String fileName = multiService.fileUpload(tokenDTO.username(), file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }


    @GetMapping
    public ResponseEntity<?> getEmailsForUser(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            return ResponseEntity.status(HttpStatus.OK).body(multiService.getEmailsForUser(tokenDTO.username()));
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping("/read")
    public ResponseEntity<?> markEmailAsRead(@RequestBody Long emailId, @RequestBody String receiverId) {
        multiService.markEmailAsRead(emailId, receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEmail(@RequestHeader("Authorization") String accessToken, @RequestBody Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            multiService.deleteEmail(emailId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @DeleteMapping("/reservation")
    public ResponseEntity<?> deleteScheduledEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long reservationId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            emailReservationService.cancelScheduledEmail(reservationId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping(value = "/schedule", consumes = {"multipart/form-data"})
    public ResponseEntity<?> scheduleEmail(
            @ModelAttribute EmailRequestDTO emailDTO,
            @RequestParam("attachments") List<MultipartFile> attachments
    ) {
        TokenDTO tokenDTO = multiService.checkToken(emailDTO.senderId());
        if (tokenDTO.isOK()) {
            SiteUser sender = multiService.getUserByUsername(emailDTO.senderId());
            multiService.scheduleEmail(
                    emailDTO.title(),
                    emailDTO.content(),
                    sender,
                    emailDTO.receiverIds(),
                    emailDTO.sendTime(),
                    attachments
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}