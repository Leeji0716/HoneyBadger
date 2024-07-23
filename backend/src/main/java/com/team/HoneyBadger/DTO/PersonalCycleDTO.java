package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record PersonalCycleDTO(Long id, String title, String content, Long startDate, Long endDate, List<String> tag) {
}
