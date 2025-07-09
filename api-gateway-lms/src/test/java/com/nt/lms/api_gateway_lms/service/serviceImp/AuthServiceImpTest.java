package com.nt.lms.api_gateway_lms.service.serviceImp;

import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.config.RsaDecryptUtil;
import com.nt.lms.api_gateway_lms.dto.AuthRequest;
import com.nt.lms.api_gateway_lms.dto.AuthResponse;
import com.nt.lms.api_gateway_lms.dto.CustomUserDetails;
import com.nt.lms.api_gateway_lms.dto.RefreshTokenRequest;
import com.nt.lms.api_gateway_lms.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceImpTest {

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtTokenManager;

    @Mock
    private RsaDecryptUtil rsaDecryptUtil;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthServiceImp authServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_successfulAuthentication() {
        AuthRequest request = new AuthRequest("dummy@nucleusteq.com", "dummyPassword");
        CustomUserDetails userDetails = new CustomUserDetails(
                "1", "dummy@nucleusteq.com", "encodedPassword",
                "Dummy User", true, List.of()
        );

        Authentication authResult = mock(Authentication.class);
        when(authResult.getPrincipal()).thenReturn(userDetails);

        when(customUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));
        when(authenticationManager.authenticate(any())).thenReturn(Mono.just(authResult));
        when(jwtTokenManager.generateAccessToken(any(), any(), any(), any())).thenReturn("dummyAccessToken");
        when(jwtTokenManager.generateRefreshToken(any(), any(), any(), any())).thenReturn("dummyRefreshToken");
        when(jwtTokenManager.getAccessTokenExpiration()).thenReturn(3600000L);

        Mono<ResponseEntity<AuthResponse>> responseMono = authServiceImp.login(request);
        ResponseEntity<AuthResponse> response = responseMono.block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("dummyAccessToken", response.getBody().getAccessToken());
        assertEquals("dummyRefreshToken", response.getBody().getRefreshToken());
    }


    @Test
    void login_inactiveUser_throwsException() {
        AuthRequest request = new AuthRequest("inactive@nucleusteq.com", "password");
        CustomUserDetails inactiveUser = new CustomUserDetails("2", "inactive@nucleusteq.com", "password", "Inactive", false, List.of());

        when(customUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(inactiveUser));

        Mono<ResponseEntity<AuthResponse>> responseMono = authServiceImp.login(request);

        ResponseEntity<AuthResponse> response = responseMono.block();
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void login_nullFields_returnsBadRequest() {
        AuthRequest request = new AuthRequest(null, null);
        Mono<ResponseEntity<AuthResponse>> responseMono = authServiceImp.login(request);
        ResponseEntity<AuthResponse> response = responseMono.block();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void refreshToken_validRequest_returnsNewAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");
        CustomUserDetails userDetails = new CustomUserDetails("1", "dummy@nucleusteq.com", "encodedPassword", "Dummy User", true, List.of());

        when(jwtTokenManager.extractEmail(anyString())).thenReturn("dummy@nucleusteq.com");
        when(customUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));
        when(jwtTokenManager.refreshAccessToken(any(), any(), any(), any(), any())).thenReturn("newAccessToken");
        when(jwtTokenManager.getAccessTokenExpiration()).thenReturn(3600000L);

        Mono<ResponseEntity<AuthResponse>> responseMono = authServiceImp.refreshToken(request);
        ResponseEntity<AuthResponse> response = responseMono.block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newAccessToken", response.getBody().getAccessToken());
    }

    @Test
    void refreshToken_invalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("badToken");
        when(jwtTokenManager.extractEmail(anyString())).thenThrow(new RuntimeException("Invalid token"));

        Mono<ResponseEntity<AuthResponse>> responseMono = authServiceImp.refreshToken(request);
        ResponseEntity<AuthResponse> response = responseMono.block();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void logout_success() {
        Mono<ResponseEntity<Map<String, String>>> responseMono = authServiceImp.logout("Bearer dummyToken");
        ResponseEntity<Map<String, String>> response = responseMono.block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logged out successfully", response.getBody().get("message"));
    }
}