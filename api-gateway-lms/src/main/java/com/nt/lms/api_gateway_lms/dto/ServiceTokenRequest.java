package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request to generate or retrieve
 * a service-to-service authentication token.
 * <p>
 * This is typically used in microservice architectures where one service
 * needs to securely communicate with another service.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTokenRequest {

    /**
     * The identifier or name of the target service for which the token is being requested.
     */
    private String targetService;
}
