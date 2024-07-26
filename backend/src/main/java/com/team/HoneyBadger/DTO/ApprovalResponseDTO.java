package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ApprovalResponseDTO(Long id,
                                  String title,
                                  String content,
                                  List<OriginFileResponseDTO> files,
                                  UserResponseDTO sender,
                                  List<ApproverResponseDTO> approvers,
                                  List<UserResponseDTO> viewers,
                                  int approvalStatus,
                                  List<String> readUsers,
                                  Long sendDate) {
}
