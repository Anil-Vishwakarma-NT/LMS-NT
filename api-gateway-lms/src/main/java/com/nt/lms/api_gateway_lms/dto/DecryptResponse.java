package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the response to a password
 * decryption or comparison request.
 * <p>
 * Indicates whether the provided plain password matches the encrypted one.
 * </p>
 */
@Data
@AllArgsConstructor
public class DecryptResponse {

    /**
     * Flag indicating whether the plain password matched the encrypted password.
     */
    private boolean passwordMatched;
}
