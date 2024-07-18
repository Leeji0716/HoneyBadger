package com.team.HoneyBadger.DTO;

import java.time.LocalDateTime;

public record PersonalCycleRequestDTO(String title, String content , LocalDateTime startDate, LocalDateTime endDate) {
}
