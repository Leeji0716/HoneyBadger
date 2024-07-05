package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.DTO.MessageResponseDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MultiService multiService;

    @MessageMapping("/message")
    @SendTo("/api/sub/message")
    public String chat(String message) {
        return message;
    }
    @MessageMapping("/message/{id}")
    @SendTo("/api/sub/message/{id}")
    public ResponseEntity<?> chat(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            MessageResponseDTO messageResponseDTO = multiService.sendMessage(id, messageRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @PostMapping("/message/{id}")
    public ResponseEntity<?> sendMessage(@PathVariable Long id, @RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            MessageResponseDTO messageResponseDTO = multiService.sendMessage(id, messageRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMessage(@RequestHeader Long messageId) {
        try {
            multiService.deleteMessage(messageId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

}
