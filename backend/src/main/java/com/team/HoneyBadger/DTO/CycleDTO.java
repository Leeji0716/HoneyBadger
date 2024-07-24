package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record CycleDTO(Long id, String title, String content, Long startDate, Long endDate, CycleTagDTO tag) {
}
