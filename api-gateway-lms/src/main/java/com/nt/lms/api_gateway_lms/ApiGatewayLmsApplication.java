package com.nt.lms.api_gateway_lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the API Gateway LMS (Learning Management System) application.
 * <p>
 * This class bootstraps the Spring Boot application and serves as the starting point
 * for the API Gateway service which routes and manages requests between clients and internal microservices.
 * </p>
 */
@SpringBootApplication
public class ApiGatewayLmsApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed during application startup.
     */
    public static void main(final String[] args) {
        SpringApplication.run(ApiGatewayLmsApplication.class, args);
        System.out.println("API Gateway running ..........................");
    }
}
