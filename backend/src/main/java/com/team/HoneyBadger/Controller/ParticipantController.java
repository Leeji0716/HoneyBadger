package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.Exception.DataDuplicateException;
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

    @PostMapping
    public ResponseEntity<?> plusParticipant(@RequestHeader Long chatroomId, @RequestHeader String username) { //채팅방에 유저 추가 or 차감 하기
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.plusParticipant(chatroomId, username);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @MessageMapping("/updateChatroom/{id}")
    @SendTo("/api/sub/updateChatroom/{id}")
    public ResponseEntity<?> updateChatRoom(@DestinationVariable Long id) {
        // 추가하기 then r=> updateChatRoom();
        ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoom(id);
        return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> minusParticipant(@RequestHeader Long chatroomId, @RequestHeader String username) { //채팅방에 유저 추가 or 차감 하기
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.minusParticipant(chatroomId, username);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
