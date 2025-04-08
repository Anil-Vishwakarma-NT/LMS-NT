package com.nt.LMS.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
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

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public final ErrorResponse handleConflictException(final ResourceConflictException ex,
                                                       final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(final UnauthorizedAccessException ex,
                                                     final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

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

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                         final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public final ErrorResponse handleInvalidRequestException(final InvalidRequestException ex,
                                                             final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                               final HttpServletRequest request) {
        String errorMessage = "Invalid input format";
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                errorMessage = String.format("Invalid value for %s. Accepted values are: %s",
                        ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                        String.join(", ", getEnumValues(ife.getTargetType())));
            }
        }
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errorMessage);
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
