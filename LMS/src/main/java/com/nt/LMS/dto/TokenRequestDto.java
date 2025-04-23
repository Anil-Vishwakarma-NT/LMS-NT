package com.nt.LMS.dto;

import lombok.Data;

@Data
public class TokenRequestDto {
    /**
     * Refresh Token cannot be null.
     */
    private String refreshToken;
}
