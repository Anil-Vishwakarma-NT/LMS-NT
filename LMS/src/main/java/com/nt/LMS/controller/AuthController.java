package com.nt.LMS.controller;

import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller to handle authentication-related endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public final class AuthController {

    /**
     * For using auth services.
     */
    @Autowired
    private AuthService authService;

    /**
     * Endpoint for user login.
     *
     * @param loginDto The login request containing email and password.
     * @return Access and refresh tokens.
     */
    @PostMapping("/login")
    public TokenResponseDto login(@Valid @RequestBody final LoginDto loginDto) {
        return authService.login(loginDto);
    }

    /**
     * Endpoint to refresh access token using refresh token.
     *
     * @param request The HTTP request containing the refresh token in the header.
     * @return A new access token and the same refresh token.
     */
    @PostMapping("/refresh-token")
    public TokenResponseDto refreshToken(final HttpServletRequest request) {
        final String refreshToken = request.getHeader("Refresh-Token");
        return authService.refreshToken(refreshToken);
    }

    /**
     * Endpoint to logout a user and invalidate refresh token.
     *
     * @param email The email of the user to logout.
     * @return Message indicating successful logout.
     */
    @PostMapping("/logout")
    public MessageOutDto logout(@RequestParam final String email) {
        return authService.logout(email);
    }
}
