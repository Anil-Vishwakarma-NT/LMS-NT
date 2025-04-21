package com.example.course_service_lms.exception;

/**
 * Exception thrown when a requested resource could not be found in the system.
 *
 * <p>This is typically used when attempting to retrieve or operate on a resource by ID or key
 * that does not exist in the database or the current context.</p>
 *
 * <p>This exception results in a 404 Not Found response, handled globally by
 * {@link GlobalExceptionHandler}.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message the detail message providing information about the missing resource
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

}
