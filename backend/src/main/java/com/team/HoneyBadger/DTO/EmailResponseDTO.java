package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailResponseDTO(Long id,
                               String title,
                               String content,
                               String senderId,
                               String senderName,
                               Long senderTime,
                               List<String> receiverIds,
                               List<OriginFileResponseDTO> files,
                               boolean status,
                               List<EmailReceiverDTO> receiverStatus) {
}