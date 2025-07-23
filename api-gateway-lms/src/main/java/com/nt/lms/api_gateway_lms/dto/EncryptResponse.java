package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the response after encrypting a plain text password.
 * <p>
 * Contains the encrypted (hashed) version of the password, which can be securely stored or used for comparison.
 * </p>
 */
@Data
@AllArgsConstructor
public class EncryptResponse {

    /**
     * The encrypted (hashed) version of the original plain text password.
     */
    private String encryptedPassword;
}
