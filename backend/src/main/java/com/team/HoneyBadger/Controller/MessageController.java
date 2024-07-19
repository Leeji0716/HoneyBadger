package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.MessageRequestDTO;
import com.team.HoneyBadger.DTO.MessageResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MultiService multiService;
    private final SimpUserRegistry simpUserRegistry;

    @DeleteMapping //메세지 삭제
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("messageId") Long messageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteMessage(messageId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body("Message DELETE SUCCESS");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/update") //메세지 읽음 처리 업데이트 -> LastMessage 저장
    public ResponseEntity<?> updateMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> list = multiService.updateMessageList(tokenDTO.username(), chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/upload") //메세지 파일 업로드
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일을 선택해주세요.");
        }
        if (tokenDTO.isOK()) try {
            String fileName = multiService.fileUpload(chatroomId, file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 중 오류 발생");
        }
        else return tokenDTO.getResponseEntity();
    }

    @MessageMapping("/message/{id}")
    @SendTo("/api/sub/message/{id}") //메세지 보내기
    public ResponseEntity<?> sendMessage(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            Set<SimpUser> users = simpUserRegistry.getUsers();
            List<String> subscriberIds = new ArrayList<>();

            for (SimpUser user : users) {
                for (SimpSession session : user.getSessions()) {
                    for (SimpSubscription subscription : session.getSubscriptions()) {
                        if (subscription.getDestination().equals("/api/sub/message/"+id)) {
                            subscriberIds.add(user.getName());
                            System.out.println(user.getName()); 
                            multiService.readMessage(id, messageRequestDTO.username());
                        }
                    }
                }
            }
            MessageResponseDTO messageResponseDTO = multiService.sendMessage(id, messageRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
//
//    @MessageMapping("/read/{id}")
//    @SendTo("/api/sub/read/{id}") //메세지 읽기 -> readUsers 리스트에 추가
//    public ResponseEntity<?> readMessages(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
//        try {
//            multiService.readMessage(id, messageRequestDTO.username());
//            return ResponseEntity.status(HttpStatus.OK).body("Read OK");
//        } catch (DataNotFoundException ex) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//    }

    @PutMapping("/read")
    public ResponseEntity<?> readMessagesTest(@RequestHeader Long id, @RequestHeader String username) {
        try {
            multiService.readMessage(id, username);
            return ResponseEntity.status(HttpStatus.OK).body("Read OK");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/readUsernames") //테스트용
    public ResponseEntity<?> readUserMessagesTest(@RequestHeader Long messageId, @RequestHeader String username) {
        try {
            List<String> readUsers = multiService.readUserMessage(messageId, username);
            return ResponseEntity.status(HttpStatus.OK).body(readUsers);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
