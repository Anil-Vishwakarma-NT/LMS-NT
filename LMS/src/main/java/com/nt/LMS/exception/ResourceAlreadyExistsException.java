package com.nt.LMS.exception;

/**
 * Exception thrown when a resource that is being created or added already exists.
 *
 * <p>This is typically used to indicate a violation of a unique constraint or business rule,
 * such as trying to create a course or bundle with a duplicate title or ID.</p>
 *
 * <p>This exception results in a 400 Bad Request response, handled globally by
 * {@link GlobalExceptionHandler}.</p>
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /**
     * Constructs a new {@code ResourceAlreadyExistsException} with the specified detail message.
     *
     * @param message the detail message explaining why the resource already exists
     */
    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

}
