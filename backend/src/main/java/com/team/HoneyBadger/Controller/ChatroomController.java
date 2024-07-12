package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.DTO.*;
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

    @GetMapping("/list") //채팅방리스트 가져오기
    public ResponseEntity<?> getChatroomList(@RequestHeader("Authorization") String accessToken,
                                             @RequestHeader(value = "keyword", defaultValue = "") String keyword) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<ChatroomResponseDTO> chatroomResponseDTOList = multiService.getChatRoomListByUser(tokenDTO.username(), keyword);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping //채팅방 찾아오기
    public ResponseEntity<?> getChatroom(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> messageResponseDTOList = multiService.getMessageList(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping //채팅방 만들기 (최초 생성 : 채팅방 이름, 참여자 이름 String)
    public ResponseEntity<?> create(@RequestHeader("Authorization") String accessToken, @RequestBody ChatroomRequestDTO chatroomRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoomType(chatroomRequestDTO);
            if (chatroomResponseDTO != null) {
                return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping //채팅방 이름 수정
    public ResponseEntity<?> updateName(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestBody ChatroomRequestDTO chatroomRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.updateChatroom(chatroomId, chatroomRequestDTO);
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

    @DeleteMapping //채팅방 삭제(참여자 테이블에서도 삭제됨.)
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteChatroom(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
            // throw new DataNotFoundException("not found chatroom");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/notification") //채팅방 공지 설정
    public ResponseEntity<?> notification(@RequestHeader("Authorization") String accessToken, @RequestHeader Long chatroomId, @RequestHeader Long MessageId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            MessageResponseDTO messageResponseDTO = multiService.notification(chatroomId, MessageId);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTO);
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
