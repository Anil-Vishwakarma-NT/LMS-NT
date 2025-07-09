package com.nt.lms.api_gateway_lms.controller;


import com.nt.lms.api_gateway_lms.Controller.TokenController;
import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.dto.ServiceTokenRequest;
import com.nt.lms.api_gateway_lms.dto.ServiceTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ServerHttpRequest request;

    @InjectMocks
    private TokenController tokenController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void shouldReturn401IfAuthHeaderIsMissing() {
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        ServiceTokenRequest serviceTokenRequest = new ServiceTokenRequest("dummyService");

        Mono<ResponseEntity<ServiceTokenResponse>> response = tokenController.getServiceToken(request, serviceTokenRequest);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCodeValue() == 401 &&
                        res.getBody().getServiceToken().equals("Missing or invalid Authorization header"))
                .verifyComplete();
    }

    @Test
    void shouldReturn401IfAuthHeaderIsInvalid() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "InvalidToken");
        when(request.getHeaders()).thenReturn(headers);

        ServiceTokenRequest serviceTokenRequest = new ServiceTokenRequest("dummyService");

        Mono<ResponseEntity<ServiceTokenResponse>> response = tokenController.getServiceToken(request, serviceTokenRequest);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCodeValue() == 401 &&
                        res.getBody().getServiceToken().equals("Missing or invalid Authorization header"))
                .verifyComplete();
    }

    @Test
    void shouldReturnServiceTokenSuccessfully() {
        String dummyAccessToken = "validToken";
        String dummyServiceToken = "generatedServiceToken";
        long expiryMillis = System.currentTimeMillis() + 100000;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER_PREFIX + dummyAccessToken);
        when(request.getHeaders()).thenReturn(headers);

        when(jwtUtil.generateServiceTokenForDirectAccess(dummyAccessToken, "dummyService"))
                .thenReturn(dummyServiceToken);
        when(jwtUtil.extractExpiration(dummyAccessToken)).thenReturn(new Date(expiryMillis));

        ServiceTokenRequest serviceTokenRequest = new ServiceTokenRequest("dummyService");

        Mono<ResponseEntity<ServiceTokenResponse>> response = tokenController.getServiceToken(request, serviceTokenRequest);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCode().is2xxSuccessful() &&
                        res.getBody().getServiceToken().equals(dummyServiceToken))
                .verifyComplete();

        verify(jwtUtil).generateServiceTokenForDirectAccess(dummyAccessToken, "dummyService");
        verify(jwtUtil).extractExpiration(dummyAccessToken);
    }

    @Test
    void shouldReturn500OnException() {
        String dummyAccessToken = "validToken";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER_PREFIX + dummyAccessToken);
        when(request.getHeaders()).thenReturn(headers);

        when(jwtUtil.generateServiceTokenForDirectAccess(dummyAccessToken, "dummyService"))
                .thenThrow(new RuntimeException("Some error"));

        ServiceTokenRequest serviceTokenRequest = new ServiceTokenRequest("dummyService");

        Mono<ResponseEntity<ServiceTokenResponse>> response = tokenController.getServiceToken(request, serviceTokenRequest);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.getStatusCodeValue() == 500 &&
                        res.getBody().getServiceToken().equals("Token processing error"))
                .verifyComplete();
    }
}
