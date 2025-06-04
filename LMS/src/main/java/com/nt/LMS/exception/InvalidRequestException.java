package com.nt.LMS.exception;

public class InvalidRequestException extends RuntimeException {
    /**
     * Exception class for Unauthorized or Invalid Request.
     * @param message
     */
    public InvalidRequestException(final String message) {
        super(message);
    }
}
