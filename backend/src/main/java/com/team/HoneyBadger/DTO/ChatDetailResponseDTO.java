package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatDetailResponseDTO(Long id, String name, List<String> users, List<MessageResponseDTO> messageResponseDTOList) {
}