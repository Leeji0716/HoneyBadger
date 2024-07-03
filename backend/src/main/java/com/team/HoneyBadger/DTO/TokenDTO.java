package com.team.HoneyBadger.DTO;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
public record TokenDTO(HttpStatus httpStatus, String username) {
    public boolean isOK() {
        return httpStatus.equals(HttpStatus.OK);
    }
    public ResponseEntity<?> getResponseEntity() {
        return getResponseEntity(null);
    }
    public <T> ResponseEntity<T> getResponseEntity(T body) {
        return ResponseEntity.status(httpStatus).body(body);
    }
}