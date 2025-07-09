package com.nt.user_service_lms.exception;

public class InvalidRequestException extends RuntimeException {
    /**
     * Exception class for Unauthorized or Invalid Request.
     * @param message
     */
    public InvalidRequestException(final String message) {
        super(message);
    }
}
