package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.DTO.MessageReservationResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Config.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messageReservation")
public class MessageReservationController {
    private final MultiService multiService;

    @PostMapping
    public ResponseEntity<?> messageReservation(@RequestHeader("Authorization") String accessToken, @RequestBody MessageReservationRequestDTO messageReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageReservationResponseDTO messageReservationResponseDTO = multiService.reservationMessage(messageReservationRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping
    public ResponseEntity<?> updateReservationMessage(@RequestHeader("Authorization") String accessToken,
                                                      @RequestHeader Long reservationMessageId,
                                                      @RequestBody MessageReservationRequestDTO messageReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageReservationResponseDTO messageReservationResponseDTO = multiService.updateReservationMessage(reservationMessageId, messageReservationRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReservationMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader Long reservationMessageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteReservationMessage(reservationMessageId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
