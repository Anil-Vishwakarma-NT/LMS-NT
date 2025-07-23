package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request to obtain a new access token
 * using a valid refresh token.
 * <p>
 * This is typically used when the original access token has expired and the client
 * needs to request a new one without re-authenticating the user.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    /**
     * The refresh token previously issued to the client, used to request a new access token.
     */
    private String refreshToken;
}
