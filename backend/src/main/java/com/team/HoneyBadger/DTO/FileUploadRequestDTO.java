package com.team.HoneyBadger.DTO;

public record FileUploadRequestDTO(String key, int index, int totalIndex, String chunk,String location, String name, int uploadType, String baseLocation) {
}
