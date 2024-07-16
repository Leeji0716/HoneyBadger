package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Enum.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MessageResponseDTO(Long id, String message, String username, String name, Long sendTime, int messageType, int readUsers) {
}
