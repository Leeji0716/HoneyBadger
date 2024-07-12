package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.Config.Exception.DataNotFoundException;
import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<?> saveFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("email_id") Long email_id, List<MultipartFile> attachments) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.emailFilesUpload(email_id, attachments);
                return ResponseEntity.status(HttpStatus.OK).body("okay");
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("files not uploaded");
            }
        } else
            return tokenDTO.getResponseEntity();
    }

    @PostMapping(value = "/schedule", consumes = {"multipart/form-data"})
    public ResponseEntity<?> scheduleEmail(@RequestHeader("Authorization") String accessToken, @RequestBody EmailReservationRequestDTO emailReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            EmailReservationResponseDTO emailReservationResponseDTO = multiService.reservationEmail(emailReservationRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(emailReservationResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping
    public ResponseEntity<?> updateEmail(@RequestHeader("Authorization") String accessToken, @RequestBody EmailReservationRequestDTO emailReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                EmailReservationResponseDTO emailReservationResponseDTO = multiService.updateEmailReservation(emailReservationRequestDTO, tokenDTO.username());
                return ResponseEntity.status(HttpStatus.OK).body(emailReservationResponseDTO);
            } catch (DataNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            } catch (RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}