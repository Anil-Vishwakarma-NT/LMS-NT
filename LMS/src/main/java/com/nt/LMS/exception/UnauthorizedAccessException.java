package com.nt.LMS.exception;

public class UnauthorizedAccessException extends RuntimeException {

    /**
     * To define Unauthorized Access by user.
     * @param message
     */
    public UnauthorizedAccessException(final String message) {
        super(message);
    }
}
