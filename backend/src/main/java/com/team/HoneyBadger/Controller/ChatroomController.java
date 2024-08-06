package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatroomController {
    private final MultiService multiService;


    @GetMapping //채팅방 메세지 가져오기
    public ResponseEntity<?> getChatroom(@RequestHeader("Authorization") String accessToken,
                                         @RequestHeader("chatroomId") Long chatroomId,
                                         @RequestHeader(value = "Page", required = false) Integer page) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);

        if (page == null || page < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 페이지를 찾을 수 없습니다.");
        }
        if (tokenDTO.isOK()) try {
            Page<MessageResponseDTO> messageResponseDTOList = multiService.getMessageList(chatroomId, page);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping //채팅방 만들기 (최초 생성 : 채팅방 이름, 참여자 이름 String)
    public ResponseEntity<?> create(@RequestHeader("Authorization") String accessToken, @RequestBody ChatroomRequestDTO chatroomRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoomType(chatroomRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping //채팅방 이름 수정
    public ResponseEntity<?> updateName(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId, @RequestBody ChatroomRequestDTO chatroomRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.updateChatroom(chatroomId, chatroomRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping //채팅방 삭제(참여자 테이블에서도 삭제됨.)
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteChatroom(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body("DELETE SUCCESS");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/file") //채팅방의 이미지 메세지 찾아오기
    public ResponseEntity<?> getChatroomByFile(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> messageResponseDTOList = multiService.getFileMessageList(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/image") //채팅방의 이미지 메세지 찾아오기
    public ResponseEntity<?> getChatroomByImage(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> messageResponseDTOList = multiService.getImageMessageList(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/link") //채팅방의 링크 메세지 찾아오기
    public ResponseEntity<?> getChatroomByLink(@RequestHeader("Authorization") String accessToken, @RequestHeader("chatroomId") Long chatroomId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<MessageResponseDTO> messageResponseDTOList = multiService.getLinkMessageList(chatroomId);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/list") //채팅방리스트 가져오기
    public ResponseEntity<?> getChatroomList(@RequestHeader("Authorization") String accessToken,
                                             @RequestHeader(value = "keyword", defaultValue = "") String keyword,
                                             @RequestHeader(value = "Page", required = false) Integer page) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);

        if (page == null || page < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 페이지를 찾을 수 없습니다.");
        }
        if (tokenDTO.isOK()) try {
            Page<ChatroomResponseDTO> chatroomResponseDTOList = multiService.getChatRoomListByUser(tokenDTO.username(), URLDecoder.decode(keyword, StandardCharsets.UTF_8), page);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTOList);
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/notification") //채팅방 공지 설정
    public ResponseEntity<?> notification(@RequestHeader("Authorization") String accessToken, @RequestBody NoticeRequestDTO noticeRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.notification(noticeRequestDTO, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO); //chatroom 리턴
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @MessageMapping("/updateChatroom/{id}")
    @SendTo("/api/sub/updateChatroom/{id}") //업데이트 채팅룸 --> 갈아 끼울 채팅방 정보
    public ResponseEntity<?> updateChatRoom(@DestinationVariable Long id, MessageRequestDTO messageRequestDTO) {
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.getChatRoomById(id, messageRequestDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
