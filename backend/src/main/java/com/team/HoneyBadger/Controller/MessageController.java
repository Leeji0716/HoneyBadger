package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.DTO.MessageResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MultiService multiService;

    @MessageMapping("/message/{id}")
    @SendTo("/api/sub/message/{id}") //메세지 보내기
    public ResponseEntity<?> sendMessage(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            MessageResponseDTO messageResponseDTO = multiService.sendMessage(id, messageRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        }
    }

    @MessageMapping("/read/{id}")
    @SendTo("/api/sub/read/{id}") //메세지 읽기 -> readUsers 리스트에 추가
    public ResponseEntity<?> readMessages(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            multiService.readMessage(id, messageRequestDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body("Read OK");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
    }

//    @PutMapping("/readTest") //메세지 읽기 테스트 -> readUsers 리스트에 추가
//    public ResponseEntity<?> readMessagesTest(@RequestHeader Long id, @RequestHeader String username) {
//        try {
//            multiService.readMessage(id, username);
//            return ResponseEntity.status(HttpStatus.OK).body("Read OK");
//        } catch (DataNotFoundException ex) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
//        }
//    }

    @GetMapping("/update") //메세지 읽음 처리 업데이트 -> LastMessage 저장
    public ResponseEntity<?> updateMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> list = multiService.updateMessageList(tokenDTO.username(), chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

//    @GetMapping("/readUsers") //readUsers 출력 테스트
//    public ResponseEntity readUsersTest(@RequestHeader Long id){
//        List<String> messageResponseDTO = multiService.getMessage(id);
//        return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
//    }

    @PostMapping("/upload") //메세지 파일 업로드
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 선택해주세요.");
        }
        if (tokenDTO.isOK()) try {
            String fileName = multiService.
                    fileUpload(chatroomId, file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping //메세지 삭제
        public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("messageId") Long messageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteMessage(messageId);
            return ResponseEntity.status(HttpStatus.OK).body("Message DELETE SUCCESS");
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
