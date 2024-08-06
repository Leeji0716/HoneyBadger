package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.ChatRoomManager;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MultiService multiService;
    private final ChatRoomManager chatRoomManager;

    @DeleteMapping //메세지 삭제
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("messageId") Long messageId) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            multiService.deleteMessage (messageId, tokenDTO.username ());
            return ResponseEntity.status (HttpStatus.OK).body ("Message DELETE SUCCESS");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        }

        else return tokenDTO.getResponseEntity ();
    }

    @GetMapping("/update") //메세지 읽음 처리 업데이트 -> LastMessage 저장
    public ResponseEntity<?> updateMessage(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            List<MessageResponseDTO> list = multiService.updateMessageList (tokenDTO.username (), chatroomId);
            return ResponseEntity.status (HttpStatus.OK).body (list);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }

    @PostMapping("/upload") //메세지 파일 업로드
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (file.isEmpty ()) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body ("파일을 선택해주세요.");
        }
        if (tokenDTO.isOK ()) try {
            String fileName = multiService.fileUpload (chatroomId, file);
            return ResponseEntity.status (HttpStatus.OK).body (fileName);
        } catch (IOException ex) {
            return ResponseEntity.status (HttpStatus.BAD_REQUEST).body ("파일 업로드 중 오류 발생");
        }
        else return tokenDTO.getResponseEntity ();
    }

    @MessageMapping("/message/{id}")
    @SendTo("/api/sub/message/{id}") //메세지 보내기
    public ResponseEntity<?> sendMessage(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            System.out.println ("test");
            MessageResponseDTO messageResponseDTO = multiService.sendMessage (id, messageRequestDTO);
            this.processMessages (id);
            return ResponseEntity.status (HttpStatus.OK).body (messageResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        }
    }

    public void processMessages(Long id) {
        BlockingQueue<String> users = chatRoomManager.getUsers (id);
        if (users != null)
            for (String reader : users) {
                multiService.readMessage (id, reader);
            }
    }

    @MessageMapping("/check/{id}")
    @SendTo("/api/sub/check/{id}") //채팅방 입장 & 다른채팅방에서 유저 정보 제거
    public ResponseEntity<?> readMessages(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            String username = messageRequestDTO.username ();
            // 모든 채팅방에서 유저를 제거
            chatRoomManager.removeUserFromAllRooms (username);
            // 새로운 채팅방에 유저 추가
            chatRoomManager.addUser (id, username);
            this.processMessages (id);

            // 현재 채팅방에 있는 유저들을 확인 (테스트를 위해 큐의 내용을 가져옴)
            BlockingQueue<String> users = chatRoomManager.getUsers (id);
            return ResponseEntity.status (HttpStatus.OK).body ("Users in chat room " + id + ": " + users);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
    }

    @PutMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestHeader("Authorization") String accessToken, @RequestHeader("name") String name) {
        chatRoomManager.removeUserFromAllRooms (name);
        return ResponseEntity.status (HttpStatus.OK).body ("OK");
    }
}
