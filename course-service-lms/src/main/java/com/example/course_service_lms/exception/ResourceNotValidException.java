package com.example.course_service_lms.exception;

/**
 * Exception thrown when a provided resource or request payload fails validation checks.
 *
 * <p>This can be used to indicate logical errors in the request that are not covered by
 * standard validation annotations, such as business rule violations or incorrect states.</p>
 *
 * <p>This exception results in a 401 Unauthorized response, as handled by
 * {@link GlobalExceptionHandler}.</p>
 */
public class ResourceNotValidException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotValidException} with the specified detail message.
     *
     * @param message the detail message explaining why the resource is not valid
     */
    public ResourceNotValidException(final String message) {
        super(message);
    }

}
