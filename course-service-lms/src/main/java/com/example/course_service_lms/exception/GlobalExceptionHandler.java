package com.example.course_service_lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the LMS Course Service.
 *
 * <p>This class captures and handles specific and general exceptions thrown across
 * the application and maps them to appropriate HTTP responses.</p>
 *
 * <p>It returns structured {@link ErrorResponse} objects or maps of validation errors
 * to improve the client-side handling of errors.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Builds a simple {@link ErrorResponse} with the given exception message and status.
     *
     * @param ex     The exception instance
     * @param status The HTTP status to associate with the error
     * @return an {@link ErrorResponse} containing the status code and error message
     */
    private ErrorResponse buildSimpleErrorResponse(final Exception ex, final HttpStatus status) {
        return new ErrorResponse(status.value(), ex.getMessage());
    }

    /**
     * Handles {@link ResourceAlreadyExistsException} and returns a 400 Bad Request response.
     *
     * @param ex the thrown exception
     * @return a structured error response with status 400
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(final ResourceAlreadyExistsException ex) {
        ErrorResponse errorResponse = buildSimpleErrorResponse(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ResourceNotValidException} and returns a 401 Unauthorized response.
     *
     * @param ex the thrown exception
     * @return a structured error response with status 401
     */
    @ExceptionHandler(ResourceNotValidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleResourceNotValidException(final ResourceNotValidException ex) {
        ErrorResponse errorResponse = buildSimpleErrorResponse(ex, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles {@link ResourceNotFoundException} and returns a 404 Not Found response.
     *
     * @param ex the thrown exception
     * @return a structured error response with status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(final ResourceNotFoundException ex) {
        ErrorResponse errorResponse = buildSimpleErrorResponse(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} (bean validation errors) and returns
     * a map of field-specific validation messages with 400 Bad Request status.
     *
     * @param ex the thrown validation exception
     * @return a map of field names to validation error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException} and returns a 400 Bad Request response
     * with details about the parameter that caused the mismatch.
     *
     * @param ex the thrown exception
     * @return a structured error response with type mismatch message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("Parameter '%s' must be of type '%s'. Provided value: '%s'",
                paramName, requiredType, value);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all uncaught exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the thrown exception
     * @return a structured error response indicating internal server error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGeneralException(final Exception ex) {
        ErrorResponse errorResponse = buildSimpleErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
