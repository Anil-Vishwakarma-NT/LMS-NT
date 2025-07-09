package com.nt.lms.api_gateway_lms.exception;

public class ResourceNotFoundException extends RuntimeException {
    /**
     * Resource is not present in database or folder.
     * @param message
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
