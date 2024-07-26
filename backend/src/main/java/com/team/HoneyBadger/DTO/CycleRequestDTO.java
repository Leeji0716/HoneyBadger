package com.team.HoneyBadger.DTO;

import java.time.LocalDateTime;

public record CycleRequestDTO(String title, String content , LocalDateTime startDate, LocalDateTime endDate,String tagName,String tagColor,String teamName) {
}
