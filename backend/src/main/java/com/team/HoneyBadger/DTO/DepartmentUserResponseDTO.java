package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record DepartmentUserResponseDTO(String name, List<UserResponseDTO> users,
                                        List<DepartmentUserResponseDTO> child) {
}
