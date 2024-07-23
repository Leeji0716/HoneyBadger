package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ApprovalResponseDTO(Long id, String title, String content, UserResponseDTO sender,  List<UserResponseDTO> approvals, List<UserResponseDTO> viewers, boolean approval  ) {
}
