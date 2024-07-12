package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
            ChatroomResponseDTO chatroomResponseDTO = multiService.plusParticipant(chatroomId, username);
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

    @MessageMapping("/updateChatroom/{id}")
    @SendTo("/api/sub/updateChatroom/{id}")
    public ResponseEntity<?> updateChatRoom(@DestinationVariable Long chatroomId) {
        // 추가하기 then r=> updateChatRoom();
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoomById(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
    }

    @DeleteMapping //채팅방에 유저 차감
    public ResponseEntity<?> minusParticipant(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestHeader String username) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.minusParticipant(chatroomId, username);
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
