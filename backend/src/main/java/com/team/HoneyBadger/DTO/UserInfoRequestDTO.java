package com.team.HoneyBadger.DTO;

import java.time.LocalDateTime;

public record UserInfoRequestDTO(String username, String name, int role, String password, String phoneNumber, LocalDateTime joinDate,
                                 String department_id) {
}
