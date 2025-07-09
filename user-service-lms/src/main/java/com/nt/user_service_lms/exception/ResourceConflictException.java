package com.nt.user_service_lms.exception;

public class ResourceConflictException extends RuntimeException {
    /**
     * Resolve Resource conflict.
     *
     * @param message
     */
    public ResourceConflictException(final String message) {
        super(message);
    }
}
