package com.team.HoneyBadger.DTO;

import java.time.LocalDateTime;

public record MessageReservationRequestDTO(Long chatroomId, String message, int messageType, LocalDateTime sendTime) {
}
