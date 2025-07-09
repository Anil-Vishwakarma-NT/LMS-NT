package com.nt.user_service_lms.exception;

public class UnauthorizedAccessException extends RuntimeException {

    /**
     * To define Unauthorized Access by user.
     * @param message
     */
    public UnauthorizedAccessException(final String message) {
        super(message);
    }
}
