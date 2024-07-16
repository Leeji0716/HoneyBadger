package com.team.HoneyBadger.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "email not found")
public class EmailReceiverNotFoundException extends RuntimeException {

    public EmailReceiverNotFoundException(String message) {
        super(message);
    }

}