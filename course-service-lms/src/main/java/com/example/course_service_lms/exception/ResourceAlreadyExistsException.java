package com.example.course_service_lms.exception;

public class ResourceAlreadyExistsException extends RuntimeException{

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

}
