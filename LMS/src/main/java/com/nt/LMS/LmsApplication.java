/**
 * This package contains classes and controllers for the Learning Management System (LMS) application.
 * The main class starts the Spring Boot application.
 */
package com.nt.LMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The entry point of the LMS application.
 * This class starts the Spring Boot application.
 */


@SpringBootApplication
@EnableFeignClients
public class LmsApplication {
    /**
     * Main method to run the application.
     *
     * @param args command-line arguments passed during the execution
     */
    public static void main(final String[] args) {
        SpringApplication.run(LmsApplication.class, args);
        System.out.println("Welcome to the Learning Management System...");
    }

}
