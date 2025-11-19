package com.reliaquest.exceptions;

public class RetryFailedException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RetryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
