package com.team.HoneyBadger.DTO;

import lombok.Builder;
@Builder
public record DepartmentResponseDTO(String name, DepartmentResponseDTO parent) {
}
