package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the authentication response
 * returned after a successful login or token refresh operation.
 * <p>
 * This object encapsulates the access and refresh tokens along with
 * token type and expiration details.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * The access token used for authenticating subsequent requests.
     */
    private String accessToken;

    /**
     * The refresh token used to obtain a new access token after expiration.
     */
    private String refreshToken;

    /**
     * The type of the token, typically "Bearer".
     */
    private String tokenType;

    /**
     * The expiration time of the access token in milliseconds or seconds,
     * depending on the implementation.
     */
    private Long expiresIn;
}
