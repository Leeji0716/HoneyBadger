package com.team.HoneyBadger.DTO;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
public record TokenDTO(HttpStatus httpStatus, String username, String error_message) {
    public boolean isOK() {
        return httpStatus.equals(HttpStatus.OK);
    }

    public ResponseEntity<?> getResponseEntity() {
        return ResponseEntity.status(httpStatus).body(error_message);
    }
}