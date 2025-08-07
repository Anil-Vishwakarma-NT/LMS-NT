package com.nt.course_service_lms.exception;

public class FileStreamingException extends RuntimeException {
    public FileStreamingException(String message) {
        super(message);
    }

    public FileStreamingException(String message, Throwable cause) {
        super(message, cause);
    }

}