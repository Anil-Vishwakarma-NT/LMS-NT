package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to verify or compare
 * an encrypted password with a plain (raw) password.
 * <p>
 * This is typically used during authentication or password validation
 * scenarios where password encryption and comparison are needed.
 * </p>
 */
@Data
@AllArgsConstructor
public class DecryptRequest {

    /**
     * The encrypted (hashed) version of the password stored in the system.
     */
    private String encryptedPassword;

    /**
     * The plain text password input provided by the user.
     */
    private String plainPassword;
}
