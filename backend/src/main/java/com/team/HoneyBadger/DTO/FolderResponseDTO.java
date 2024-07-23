package com.team.HoneyBadger.DTO;

import lombok.Builder;

import java.util.List;
@Builder
public record FolderResponseDTO (String name, List<FolderResponseDTO> child){
}
