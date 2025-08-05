/**
 * This package contains classes and controllers for the Learning Management System (LMS) application.
 * The main class starts the Spring Boot application.
 */
package com.nt.user_service_lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The entry point of the LMS application.
 * This class starts the Spring Boot application.
 */

@SpringBootApplication
@EnableFeignClients
public final class UserServiceLmsApplication {
    /**
     * Private constructor to prevent instantiation.
     * Required to satisfy Checkstyle utility class rule (even though this is not a utility class).
     */
    private UserServiceLmsApplication() {
    }

    /**
     * Main method to run the application.
     *
     * @param args command-line arguments passed during the execution
     */
    public static void main(final String[] args) {
        SpringApplication.run(UserServiceLmsApplication.class, args);
        System.out.println("Welcome to th e Learning Management System...");
    }

}
