package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Enum.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageResponseDTO(Long id, String message, String username, Long sendTime, MessageType messageType) {
}
