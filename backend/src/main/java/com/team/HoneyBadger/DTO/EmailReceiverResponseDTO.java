package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailReceiverResponseDTO (Long id,
                                        boolean status,
                                        String receiverId, // 수신자 ID
                                        List<EmailResponseDTO> emailResponseDTOs) {
}
