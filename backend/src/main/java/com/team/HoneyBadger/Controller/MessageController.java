package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.DTO.MessageResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MultiService multiService;

    @MessageMapping("/message/{id}")
    @SendTo("/api/sub/message/{id}")
    public ResponseEntity<?> sendMessage(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        MessageResponseDTO messageResponseDTO = multiService.sendMessage(id, messageRequestDTO, messageRequestDTO.username());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, @RequestHeader("room_id") Long roomId, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 선택해주세요.");
        if (tokenDTO.isOK()) try {
            String fileName = multiService.fileUpload(roomId, file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader Long messageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteMessage(messageId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }

}
