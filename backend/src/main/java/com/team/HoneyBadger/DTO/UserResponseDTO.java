package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record UserResponseDTO(String username, String name, String phoneNumber, int role, Long createDate,Long modifyDate, String url) {

}
