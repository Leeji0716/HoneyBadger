package com.team.HoneyBadger.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "related entity can't remove")
public class RelatedException extends RuntimeException {
    public RelatedException(String message) {
        super(message);
    }
}