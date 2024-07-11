package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/emailReservation")
@RequiredArgsConstructor
public class EmailReservationController {
    private final MultiService multiService;


    @DeleteMapping("/reservation")
    public ResponseEntity<?> deleteScheduledEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long reservationId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            multiService.deleteEmailReservation(reservationId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping("/files")
    public ResponseEntity<?> saveFiles(List<MultipartFile> attachments) {

        List<String> urls = new ArrayList<>(); // 저장 -> url 담아서 반환

        return ResponseEntity.status(HttpStatus.OK).body(urls);
    }

    @PostMapping(value = "/schedule", consumes = {"multipart/form-data"})
    public ResponseEntity<?> scheduleEmail(@RequestHeader("Authorization") String accessToken, @RequestBody EmailRequestDTO emailDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
//            SiteUser sender = multiService.getUserByUsername(tokenDTO.username());
//            multiService.scheduleEmail(
//                    emailDTO.title(),
//                    emailDTO.content(),
//                    sender,
//                    emailDTO.receiverIds(),
//                    emailDTO.sendTime(),
//                    attachments
//            );
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}
