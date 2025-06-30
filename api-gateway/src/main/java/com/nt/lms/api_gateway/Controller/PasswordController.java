package com.nt.lms.api_gateway.Controller;

import com.nt.lms.api_gateway.dto.DecryptRequest;
import com.nt.lms.api_gateway.dto.DecryptResponse;
import com.nt.lms.api_gateway.dto.EncryptRequest;
import com.nt.lms.api_gateway.dto.EncryptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client-api/password")
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Encrypt password
    @PostMapping("/encrypt")
    public ResponseEntity<EncryptResponse> encrypt(@RequestBody EncryptRequest request) {
        return new ResponseEntity<>(new EncryptResponse(passwordEncoder.encode(request.getPassword())), HttpStatus.OK);
    }

    // Verify password
    @PostMapping("/verify")
    public ResponseEntity<DecryptResponse> verify(@RequestBody DecryptRequest request) {
        boolean matched =  passwordEncoder.matches(request.getPlainPassword(), request.getEncryptedPassword());
        DecryptResponse decryptResponse = new DecryptResponse(matched);
        return new ResponseEntity<>(decryptResponse,HttpStatus.OK);

    }
}
