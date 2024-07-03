package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record AuthResponseDTO(String tokenType, String accessToken, String refreshToken) {

}