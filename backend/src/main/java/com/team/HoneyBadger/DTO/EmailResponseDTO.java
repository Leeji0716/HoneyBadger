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
                               List<FileResponseDTO> files,
                               boolean status,
                               // 추가할 receivers 필드
                               List<EmailReceiverResponseDTO> receivers) {
}
