package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ChatroomResponseDTO;
import com.team.HoneyBadger.DTO.ParticipantRequestDTO;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.Participant;
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

    @PostMapping
    public ResponseEntity<?> plusParticipant(@RequestBody ParticipantRequestDTO participantRequestDTO){ //채팅방에 유저 추가 or 차감 하기
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.plusParticipant(participantRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> minusParticipant(@RequestBody ParticipantRequestDTO participantRequestDTO){ //채팅방에 유저 추가 or 차감 하기
        try {
            ChatroomResponseDTO chatroomResponseDTO = multiService.minusParticipant(participantRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(chatroomResponseDTO);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
