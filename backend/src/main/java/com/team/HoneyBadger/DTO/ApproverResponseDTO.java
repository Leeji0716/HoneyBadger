package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ApproverResponseDTO(UserResponseDTO approver, int approverStatus, Long approvalDate) {
}
