package com.nt.lms.api_gateway.Controller;

import com.nt.lms.api_gateway.dto.*;
import com.nt.lms.api_gateway.service.serviceImp.AuthServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/client-api/auth")
public class AuthController {

    /**
     * For using auth services.
     */
    @Autowired
    private AuthServiceImp authService;

    /**
     * Endpoint for user login.
     *
     * @param  request The login request containing email and password.
     * @return Access and refresh tokens.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(@RequestHeader("Authorization") String authHeader) {
        return authService.logout(authHeader);
    }
}