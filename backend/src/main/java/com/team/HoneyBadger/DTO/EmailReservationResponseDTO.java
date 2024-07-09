package com.team.HoneyBadger.DTO;

import java.util.List;

public record EmailReservationResponseDTO(Long id, String title, String content, String senderId, String senderName,
                                         Long senderTime, List<String> receiverIds) {
}
