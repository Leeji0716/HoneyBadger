package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailReservationResponseDTO(Long id,
                                          String title,
                                          String content,
                                          Long senderTime,
                                          List<String> receiverIds,
                                          List<OriginFileResponseDTO> files) {
}