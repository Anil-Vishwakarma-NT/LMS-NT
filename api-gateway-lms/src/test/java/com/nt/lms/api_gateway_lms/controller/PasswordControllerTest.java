package com.nt.lms.api_gateway_lms.controller;

import com.nt.lms.api_gateway_lms.Controller.PasswordController;
import com.nt.lms.api_gateway_lms.dto.DecryptRequest;
import com.nt.lms.api_gateway_lms.dto.DecryptResponse;
import com.nt.lms.api_gateway_lms.dto.EncryptRequest;
import com.nt.lms.api_gateway_lms.dto.EncryptResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class PasswordControllerTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordController passwordController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testEncrypt() {
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        EncryptRequest request = new EncryptRequest(rawPassword);
        ResponseEntity<EncryptResponse> response = passwordController.encrypt(request);

        assertEquals(encodedPassword, response.getBody().getEncryptedPassword());
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void testVerifyMatch() {
        DecryptRequest request = new DecryptRequest("encoded", "plain");

        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);

        ResponseEntity<DecryptResponse> response = passwordController.verify(request);

        assertEquals(true, response.getBody().isPasswordMatched());
        verify(passwordEncoder).matches("plain", "encoded");
    }


    @Test
    void testVerifyMismatch() {
        DecryptRequest request = new DecryptRequest("encoded", "plain");

        when(passwordEncoder.matches("plain", "encoded")).thenReturn(false);

        ResponseEntity<DecryptResponse> response = passwordController.verify(request);

        assertEquals(false, response.getBody().isPasswordMatched());
        verify(passwordEncoder).matches("plain", "encoded");
    }

}
