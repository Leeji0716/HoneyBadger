package com.team.HoneyBadger.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "not correct File type")
public class InvalidFileTypeException extends RuntimeException{
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
