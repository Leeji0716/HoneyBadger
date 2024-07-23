package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ApprovalResponseDTO(Long id, String title, String content, UserResponseDTO sender,  List<ApproverResponseDTO> approvers, List<UserResponseDTO> viewers, boolean approval, List<String> readUsers  ) {
}
