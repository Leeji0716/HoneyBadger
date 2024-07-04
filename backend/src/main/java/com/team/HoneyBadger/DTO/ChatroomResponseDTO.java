package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Entity.Participant;

import java.util.List;

public record ChatroomResponseDTO(Long id, String name, List<String> users) {
}
