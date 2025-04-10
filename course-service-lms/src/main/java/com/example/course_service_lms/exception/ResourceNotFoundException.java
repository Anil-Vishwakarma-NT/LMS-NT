package com.example.course_service_lms.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(final String message) {
        super(message);
    }

}
