package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record FileUploadResponseDTO(String key, int index, String name) {
}
