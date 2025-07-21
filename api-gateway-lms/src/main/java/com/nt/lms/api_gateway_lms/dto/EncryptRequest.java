package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to encrypt a plain text password.
 * <p>
 * Typically used when storing or transmitting passwords securely by converting
 * them into an encrypted (hashed) format.
 * </p>
 */
@Data
@AllArgsConstructor
public class EncryptRequest {

    /**
     * The plain text password to be encrypted.
     */
    private String password;
}
