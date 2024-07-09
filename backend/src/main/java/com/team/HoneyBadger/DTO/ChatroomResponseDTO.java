package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Entity.Message;
import com.team.HoneyBadger.Entity.Participant;
import lombok.Builder;

import java.util.List;
@Builder
public record ChatroomResponseDTO(Long id, String name, List<String> users, List<MessageResponseDTO> messageResponseDTOList) {
}
