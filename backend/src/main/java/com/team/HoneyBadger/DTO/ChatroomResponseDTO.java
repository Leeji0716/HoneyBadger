package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;
@Builder
public record ChatroomResponseDTO(Long id, String name, List<UserResponseDTO> users, MessageResponseDTO latestMessage, MessageResponseDTO notification, int alarmCount, Long createDate) {
}
