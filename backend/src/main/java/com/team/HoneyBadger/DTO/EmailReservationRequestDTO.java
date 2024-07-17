package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EmailReservationRequestDTO(Long id, String title, String content, List<String> receiverIds,
                                         LocalDateTime sendTime, List<String> files) {
}