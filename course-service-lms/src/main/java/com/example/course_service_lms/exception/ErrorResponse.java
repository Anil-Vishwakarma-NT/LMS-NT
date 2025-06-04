package com.example.course_service_lms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A structured error response used for communicating error details in API responses.
 *
 * <p>This class is typically returned in the response body when an exception is thrown
 * in the application, allowing clients to understand what went wrong.</p>
 *
 * <p>Example JSON response:
 * <pre>{@code
 * {
 *   "status": 404,
 *   "message": "Resource not found"
 * }
 * }</pre>
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * HTTP status code associated with the error (e.g., 400, 404, 500).
     */
    private int status;

    /**
     * A human-readable error message that describes the cause of the error.
     */
    private String message;
}
