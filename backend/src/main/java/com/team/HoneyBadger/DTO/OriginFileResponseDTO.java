package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record OriginFileResponseDTO(String key, String original_name, String value) {
}
