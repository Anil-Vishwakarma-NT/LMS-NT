package com.nt.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    /**
     * AccessToken cannot be null.
     */
    private String accessToken;
    /**
     * RefreshToken cannot be null.
     */
    private String refreshToken;
    /**
     * Token type .
     */
    private String tokenType = "Bearer";

}
