package com.nt.lms.api_gateway.controller;

import com.nt.lms.api_gateway.Controller.AuthController;
import com.nt.lms.api_gateway.dto.*;
import com.nt.lms.api_gateway.service.serviceImp.AuthServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AuthControllerTest {

    @Mock
    private AuthServiceImp authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testLogin() {
        AuthRequest request = new AuthRequest("name", "password");
        AuthResponse response = new AuthResponse("accessToken", "refreshToken", "name", 1L);

        when(authService.login(request)).thenReturn(Mono.just(ResponseEntity.ok(response)));

        StepVerifier.create(authController.login(request))
                .expectNextMatches(entity -> entity.getBody().getAccessToken().equals("accessToken"))
                .verifyComplete();

        verify(authService, times(1)).login(request);
    }

    @Test
    void testRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("refreshToken");
        AuthResponse response = new AuthResponse("newAccessToken", "newRefreshToken", "name", 1L);

        when(authService.refreshToken(request)).thenReturn(Mono.just(ResponseEntity.ok(response)));

        StepVerifier.create(authController.refreshToken(request))
                .expectNextMatches(entity -> entity.getBody().getAccessToken().equals("newAccessToken"))
                .verifyComplete();

        verify(authService).refreshToken(request);
    }

    @Test
    void testLogout() {
        String dummyAuthHeader = "Bearer token";
        Map<String, String> response = Collections.singletonMap("message", "Logged out");

        when(authService.logout(dummyAuthHeader)).thenReturn(Mono.just(ResponseEntity.ok(response)));

        StepVerifier.create(authController.logout(dummyAuthHeader))
                .expectNextMatches(entity -> entity.getBody().get("message").equals("Logged out"))
                .verifyComplete();

        verify(authService).logout(dummyAuthHeader);
    }
}
