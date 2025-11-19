package com.reliaquest.exceptions;

public class InvalidResponseException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidResponseException(String message) {
        super(message);
    }
}
