package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final MultiService multiService;

    @PostMapping //채팅방에 유저 추가
    public ResponseEntity<?> plusParticipant(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestHeader String username) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.plusParticipant(chatroomId, username, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping //채팅방에 유저 차감
    public ResponseEntity<?> minusParticipant(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestHeader String username) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.minusParticipant(chatroomId, username, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
