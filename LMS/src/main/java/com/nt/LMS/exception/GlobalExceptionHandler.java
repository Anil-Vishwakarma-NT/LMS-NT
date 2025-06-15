package com.nt.LMS.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A centralized exception handler for all REST controllers.
 * Handles various custom and Spring exceptions globally.
 */
@ControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Handles ResourceConflictException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return standardized error response
     */
    @ExceptionHandler(ResourceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictException(final ResourceConflictException ex,
                                                 final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }


    /**
     * Handles UnauthorizedAccessException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return standardized error response
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(final UnauthorizedAccessException ex,
                                                     final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnauthorizedException(final ResourceNotValidException ex,
                                                     final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUnauthorizedException(final ResourceAlreadyExistsException ex,
                                                     final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    /**
     * Handles validation exceptions when method arguments fail constraints.
     *
     * @param ex      the validation exception
     * @param request the HTTP request
     * @return standardized error response with validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException ex,
                                                   final HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(",", errors);
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errorMessage);
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return standardized error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                         final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    /**
     * Handles InvalidRequestException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return standardized error response
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidRequestException(final InvalidRequestException ex,
                                                       final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Handles bad input format errors.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return standardized error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                               final HttpServletRequest request) {
        String errorMessage = "Invalid input format";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                errorMessage = String.format("Invalid value for %s. Accepted values are: %s",
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                        String.join(", ", getEnumValues(ife.getTargetType())));
            }
        }
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    /**
     * Custom error response structure.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {

        /**
         * The timestamp when the error occurred.
         */
        private LocalDateTime timestamp;

        /**
         * The HTTP status code.
         */
        private int status;

        /**
         * The human-readable error message.
         */
        private String message;
    }

    /**
     * Utility method to extract enum values from a class.
     *
     * @param enumClass the enum class
     * @return list of enum constant names
     */
    private List<String> getEnumValues(final Class<?> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
