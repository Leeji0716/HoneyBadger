package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;
@Builder
public record CycleResponseDTO(List<CycleDTO> cycleDTOList, boolean holiday, String holidayTitle){
}

