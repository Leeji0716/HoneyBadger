package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomRequestDTO;
import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.Module.ChatroomService;
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

    @GetMapping("/list")
    public ResponseEntity<?> getChatroomList(@RequestHeader("Authorization") String accessToken) { //채팅방 가져오기(찾아오기)
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<ChatroomResponseDTO> chatroomResponseDTOList = multiService.getChatRoomListByUser(tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<?> getChatroom(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId) { //채팅방 가져오기(찾아오기)
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoom(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String accessToken, @RequestBody ChatroomRequestDTO chatroomRequestDTO) { //채팅방 만들기 (최초 생성 : 채팅방 이름, 참여자 이름 String)
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoomType(chatroomRequestDTO);
            if (chatroomResponseDTO != null) {
                return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping
    public ResponseEntity<?> updateName(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestBody ChatroomRequestDTO chatroomRequestDTO) { //채팅방 이름 수정
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.updateChatroom(chatroomId, chatroomRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId) { //채팅방 삭제(참여자 테이블에서도 삭제됨.)
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteChatroom(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
            // throw new DataNotFoundException("not found chatroom");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
