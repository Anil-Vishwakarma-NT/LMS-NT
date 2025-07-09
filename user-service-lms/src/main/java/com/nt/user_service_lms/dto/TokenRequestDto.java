package com.nt.user_service_lms.dto;

import lombok.Data;

@Data
public class TokenRequestDto {
    /**
     * Refresh Token cannot be null.
     */
    private String refreshToken;
}
