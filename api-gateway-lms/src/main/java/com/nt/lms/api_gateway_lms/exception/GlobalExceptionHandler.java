package com.nt.lms.api_gateway_lms.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A centralized exception handler for all WebFlux controllers.
 * <p>
 * This class captures and processes various types of exceptions thrown by REST endpoints,
 * and returns consistent and meaningful error responses to the client.
 * </p>
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Handles validation errors when request body constraints are violated.
     *
     * @param ex       the validation exception
     * @param exchange the server web exchange context
     * @return a {@link Mono} wrapping an {@link ErrorResponse} with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ErrorResponse> handleValidationException(final MethodArgumentNotValidException ex,
                                                         final ServerWebExchange exchange) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed: " + errorMessage
        ));
    }

    /**
     * Handles cases where a requested resource is not found.
     *
     * @param ex       the {@link ResourceNotFoundException}
     * @param exchange the server web exchange context
     * @return a {@link Mono} wrapping an {@link ErrorResponse} with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ErrorResponse> handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                               final ServerWebExchange exchange) {
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        ));
    }

    /**
     * Handles malformed or invalid input in HTTP requests, such as type mismatches or unparseable JSON.
     * If the error is related to an enum, a list of valid values is returned.
     *
     * @param ex       the {@link HttpMessageNotReadableException}
     * @param exchange the server web exchange context
     * @return a {@link Mono} wrapping an {@link ErrorResponse} with parsing error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                                     final ServerWebExchange exchange) {
        String errorMessage = "Invalid input format";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                errorMessage = String.format("Invalid value for %s. Accepted values are: %s",
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                        String.join(", ", getEnumValues(ife.getTargetType()))
                );
            }
        }

        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
        ));
    }

    /**
     * A standardized structure for error responses returned to the client.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {

        /**
         * The timestamp when the error occurred.
         */
        private LocalDateTime timestamp;

        /**
         * The HTTP status code associated with the error.
         */
        private int status;

        /**
         * A human-readable message describing the error.
         */
        private String message;
    }

    /**
     * Utility method to extract valid enum values for a given enum class.
     *
     * @param enumClass the enum class
     * @return a list of valid enum values as strings
     */
    private List<String> getEnumValues(final Class<?> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
