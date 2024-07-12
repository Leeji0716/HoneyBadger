package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record PasswordChangeDTO(String prePassword, String newPassword) {
}
