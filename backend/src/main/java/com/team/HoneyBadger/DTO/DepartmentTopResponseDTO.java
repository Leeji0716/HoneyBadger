package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record DepartmentTopResponseDTO(String name, List<DepartmentTopResponseDTO> child, String url, Long createDate,
                                       Long modifyDate, int role) {
}
