package com.team.HoneyBadger.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "data not same")
public class DataNotSameException extends RuntimeException {
    public DataNotSameException(String message) {
        super(message);
    }
}