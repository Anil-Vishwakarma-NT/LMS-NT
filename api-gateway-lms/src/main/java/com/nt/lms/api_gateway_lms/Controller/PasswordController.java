package com.nt.lms.api_gateway_lms.Controller;

import com.nt.lms.api_gateway_lms.dto.DecryptRequest;
import com.nt.lms.api_gateway_lms.dto.DecryptResponse;
import com.nt.lms.api_gateway_lms.dto.EncryptRequest;
import com.nt.lms.api_gateway_lms.dto.EncryptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling password encryption and verification operations.
 * This controller provides endpoints for encoding passwords and verifying
 * plain text passwords against their encoded counterparts.
 *
 * @author Learning Management System Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/lms/api/client-api/password")
public class PasswordController {

    /**
     * Password encoder instance used for encoding and matching passwords.
     * This encoder provides secure password hashing functionality.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Encrypts a plain text password using the configured password encoder.
     *
     * @param request the encryption request containing the plain text password to be encoded
     * @return ResponseEntity containing the encrypted password response with HTTP 200 status
     * @throws IllegalArgumentException if the request or password is null or empty
     */
    @PostMapping("/encrypt")
    public ResponseEntity<EncryptResponse> encrypt(@RequestBody final EncryptRequest request) {
        return new ResponseEntity<>(new EncryptResponse(passwordEncoder.encode(request.getPassword())), HttpStatus.OK);
    }

    /**
     * Verifies if a plain text password matches the encrypted password.
     * This method uses the password encoder to compare the plain text password
     * with its encoded version without decrypting the encoded password.
     *
     * @param request the decrypt request containing both plain text and encrypted passwords
     * @return ResponseEntity containing the verification result with HTTP 200 status
     * @throws IllegalArgumentException if the request or any of its passwords is null
     */
    @PostMapping("/verify")
    public ResponseEntity<DecryptResponse> verify(@RequestBody final DecryptRequest request) {
        boolean matched = passwordEncoder.matches(request.getPlainPassword(), request.getEncryptedPassword());
        DecryptResponse decryptResponse = new DecryptResponse(matched);
        return new ResponseEntity<>(decryptResponse, HttpStatus.OK);
    }
}
