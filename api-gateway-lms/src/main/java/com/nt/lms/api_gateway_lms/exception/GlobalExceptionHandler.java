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
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ErrorResponse> handleValidationException(final MethodArgumentNotValidException ex,
                                                         final ServerWebExchange exchange) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        return Mono.just(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Validation failed: " + errorMessage));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ErrorResponse> handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                               final ServerWebExchange exchange) {
        return Mono.just(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                                     final ServerWebExchange exchange) {
        String errorMessage = "Invalid input format";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                errorMessage = String.format("Invalid value for %s. Accepted values are: %s",
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                        String.join(", ", getEnumValues(ife.getTargetType())));
            }
        }
        return Mono.just(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String message;
    }

    private List<String> getEnumValues(final Class<?> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
