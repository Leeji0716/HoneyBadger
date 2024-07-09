package com.team.HoneyBadger.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record MessageRequestDTO(String message, String username, int messageType){
}
