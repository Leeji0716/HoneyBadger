package com.team.HoneyBadger.Config.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "data duplicate found")
public class DataDuplicateException extends RuntimeException {
    public DataDuplicateException(String message) {
        super(message);
    }
}