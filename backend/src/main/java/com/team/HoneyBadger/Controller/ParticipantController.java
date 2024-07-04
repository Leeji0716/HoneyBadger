package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.ParticipantRequestDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Service.Module.ParticipantService;
import com.team.HoneyBadger.Service.Module.UserService;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final MultiService multiService;
    private final UserService userService;
    private final ParticipantService participantService;
    @PostMapping
    public ResponseEntity<?> plusParticipant(@RequestBody ParticipantRequestDTO participantRequestDTO){ //채팅방에 유저 추가 하기
        try {
            Chatroom chatroom = multiService.getChatroom(participantRequestDTO.chatroomId());
            SiteUser siteUser = userService.get(participantRequestDTO.username());
            participantService.save(siteUser, chatroom);
            List<String> users = chatroom.getParticipants().stream()
                    .map(participant -> participant.getUser().getUsername())
                    .toList();
            ChatroomResponseDTO chatroomResponseDTO = new ChatroomResponseDTO(chatroom.getId(), chatroom.getName(), users);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
