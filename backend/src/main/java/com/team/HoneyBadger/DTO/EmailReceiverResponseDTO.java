package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record EmailReceiverResponseDTO (Long id,
                                        boolean status,
                                        String receiverId, // 수신자 ID
                                        EmailResponseDTO emailResponseDTO) {
}
