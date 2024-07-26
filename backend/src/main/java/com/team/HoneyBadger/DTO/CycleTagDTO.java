package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record CycleTagDTO(Long id,String name,String color) {
}
