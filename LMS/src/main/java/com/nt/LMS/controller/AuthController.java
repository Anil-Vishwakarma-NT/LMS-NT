package com.nt.LMS.controller;

import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.StandardResponseOutDTO;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public ResponseEntity<StandardResponseOutDTO<TokenResponseDto>> login(@Valid @RequestBody final LoginDto loginDto) {
        StandardResponseOutDTO<TokenResponseDto> response = authService.login(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint to refresh access token using refresh token.
     *
     * @param request The HTTP request containing the refresh token in the header.
     * @return A new access token and the same refresh token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<StandardResponseOutDTO<TokenResponseDto>> refreshToken(final HttpServletRequest request) {
        final String refreshToken = request.getHeader("Refresh-Token");
           StandardResponseOutDTO response = authService.refreshToken(refreshToken);
           return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    /**
     * Endpoint to logout a user and invalidate refresh token.
     *
     * @return Message indicating successful logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        StandardResponseOutDTO response = authService.logout(email);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
