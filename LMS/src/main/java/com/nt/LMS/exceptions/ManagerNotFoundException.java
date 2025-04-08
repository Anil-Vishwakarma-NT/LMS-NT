package com.nt.LMS.exceptions;

public class ManagerNotFoundException extends RuntimeException {
    public ManagerNotFoundException(String message) {
        super(message);
    }
}
