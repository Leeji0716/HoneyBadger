package com.team.HoneyBadger.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record EmailRequestDTO(String title, String content, String senderId, List<String> receiverIds, LocalDateTime sendTime) {
}