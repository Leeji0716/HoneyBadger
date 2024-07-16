package com.team.HoneyBadger.Exception;

public class EmailReceiverNotFoundException extends RuntimeException {
    public EmailReceiverNotFoundException() {
        super();
    }

    public EmailReceiverNotFoundException(String message) {
        super(message);
    }

    public EmailReceiverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailReceiverNotFoundException(Throwable cause) {
        super(cause);
    }
}