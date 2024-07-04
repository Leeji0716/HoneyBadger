package com.team.HoneyBadger.DTO;

import java.util.List;

public record ChatroomRequestDTO(String name, List<String> users) {
}
