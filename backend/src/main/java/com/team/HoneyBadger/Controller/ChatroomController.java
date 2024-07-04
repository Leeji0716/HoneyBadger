package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomRequestDTO;
import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatroomController {
    private final MultiService multiService;

    @GetMapping
    public ResponseEntity<?> getChatroom(@RequestHeader Long chatroomId){ //채팅방 가져오기(찾아오기)
        try {
            Chatroom chatroom = multiService.getChatroom(chatroomId);
            List<String> users = chatroom.getParticipants().stream()
                    .map(participant -> participant.getUser().getUsername())
                    .toList();
            ChatroomResponseDTO chatroomResponseDTO = new ChatroomResponseDTO(chatroom.getId(), chatroom.getName(), users);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ChatroomRequestDTO chatroomRequestDTO){ //채팅방 만들기 (최초 생성 : 채팅방 이름, 참여자 이름 String)

            Chatroom chatroom = multiService.createChatroom(chatroomRequestDTO);
            List<String> users = chatroom.getParticipants().stream()
                    .map(participant -> participant.getUser().getUsername())
                    .toList();
            ChatroomResponseDTO chatroomResponseDTO = new ChatroomResponseDTO(chatroom.getId(), chatroom.getName(), users);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
    }

    @PutMapping
    public ResponseEntity<?> updateName(@RequestHeader Long chatroomId, @RequestBody ChatroomRequestDTO chatroomRequestDTO){ //채팅방 이름 수정
        try {
            Chatroom chatroom = multiService.updateChatroom(chatroomId, chatroomRequestDTO);
            ChatroomResponseDTO chatroomResponseDTO = new ChatroomResponseDTO(chatroom.getId(), chatroom.getName(), chatroomRequestDTO.users());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestHeader Long chatroomId){ //채팅방 삭제(참여자 테이블에서도 삭제됨.)
        try {
            Chatroom chatroom = multiService.getChatroom(chatroomId);
            multiService.deleteChatroom(chatroom);
            return ResponseEntity.status(HttpStatus.OK).body(null);
            // throw new DataNotFoundException("not found chatroom");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
