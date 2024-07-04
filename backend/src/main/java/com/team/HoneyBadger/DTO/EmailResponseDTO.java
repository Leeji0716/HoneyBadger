package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailResponseDTO(Long id, String title, String content, String senderId, List<String> receiverIds) {

}