package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record EmailReceiverResponseDTO (Long id, boolean status, EmailResponseDTO emailResponseDTO) {
}
