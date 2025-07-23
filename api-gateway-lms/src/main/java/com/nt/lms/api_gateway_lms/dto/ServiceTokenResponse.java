package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the response containing a service-to-service
 * authentication token.
 * <p>
 * This token is typically used for secure communication between microservices.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTokenResponse {

    /**
     * The generated service token used for authenticating inter-service requests.
     */
    private String serviceToken;
}
