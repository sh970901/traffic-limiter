package com.blog4j.limiter.frame.exception;

public class LimiterException extends RuntimeException {
    private final String message;

    public LimiterException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}