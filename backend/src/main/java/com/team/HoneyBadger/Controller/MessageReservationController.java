package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.MessageReservationRequestDTO;
import com.team.HoneyBadger.DTO.MessageReservationResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messageReservation")
public class MessageReservationController {
    private final MultiService multiService;

    @GetMapping
    public ResponseEntity<?> getMessageReservation(@RequestHeader("Authorization") String accessToken, @RequestHeader("reservationMessageId") Long reservationMessageId){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageReservationResponseDTO messageReservationResponseDTO = multiService.getMessageReservationById(reservationMessageId);
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        }catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping
    public ResponseEntity<?> messageReservation(@RequestHeader("Authorization") String accessToken, @RequestBody MessageReservationRequestDTO messageReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageReservationResponseDTO messageReservationResponseDTO = multiService.reservationMessage(messageReservationRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping //예약 메일 수정
    public ResponseEntity<?> updateReservationMessage(@RequestHeader("Authorization") String accessToken,
                                                      @RequestHeader("reservationMessageId") Long reservationMessageId,
                                                      @RequestBody MessageReservationRequestDTO messageReservationRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageReservationResponseDTO messageReservationResponseDTO = multiService.updateReservationMessage(reservationMessageId, messageReservationRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReservationMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("reservationMessageId") Long reservationMessageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteReservationMessage(reservationMessageId);
            return ResponseEntity.status(HttpStatus.OK).body("Reservation Message Delete Success");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getMessageReservationList(@RequestHeader("Authorization") String accessToken,  @RequestHeader(value = "Page", required = false) Integer page){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (page == null || page < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 페이지를 찾을 수 없습니다.");
        }
        if (tokenDTO.isOK()) try {
            Page<MessageReservationResponseDTO> messageReservationResponseDTO = multiService.getMessageReservationByUser(tokenDTO.username(), page);
            return ResponseEntity.status(HttpStatus.OK).body(messageReservationResponseDTO);
        }catch (DataNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
