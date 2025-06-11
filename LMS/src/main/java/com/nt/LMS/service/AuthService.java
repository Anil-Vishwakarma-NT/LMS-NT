package com.nt.LMS.service;

import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.outDTO.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;

/**
 * Service interface for authentication operations like login,
 * token refresh, and logout.
 */
public interface AuthService {

    /**
     * Authenticates a user using provided credentials.
     *
     * @param loginDto the login credentials
     * @return a token response containing access and refresh tokens
     */
    TokenResponseDto login(LoginDto loginDto);

    /**
     * Generates a new access token using the provided refresh token.
     *
     * @param refreshToken the refresh token
     * @return a new token response
     */
    TokenResponseDto refreshToken(String refreshToken);

    /**
     * Logs the user out by invalidating the refresh token.
     *
     * @param email the user's email address
     * @return a message indicating logout success
     */
    MessageOutDto logout(String email);
}
