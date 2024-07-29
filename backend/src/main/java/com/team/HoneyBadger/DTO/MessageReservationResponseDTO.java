package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record MessageReservationResponseDTO(Long id, Long chatroomId, String message, String username, String name, Long reservationDate, int messageType) {
}
