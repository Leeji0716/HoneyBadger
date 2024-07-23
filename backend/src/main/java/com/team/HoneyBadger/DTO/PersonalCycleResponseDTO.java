package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;
@Builder
public record PersonalCycleResponseDTO (List<PersonalCycleDTO> personalCycleDTOList, boolean holiday, String holidayTitle){
}

