package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record FileResponseDTO(String name, int type, String url, long size, long createDate, long modifyDate) {
}
