package com.nt.LMS.controller;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginDto loginDto) {
        log.info("Login attempt for email: {}", loginDto.getEmail());
        TokenResponseDto response = authService.login(loginDto);
        log.info("Login successful for email: {}", loginDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        log.info("Refresh token attempt: {}", refreshToken);
        TokenResponseDto response = authService.refreshToken(refreshToken);
        log.info("Access token refreshed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout/{email}")
    public ResponseEntity<MessageOutDto> logout(@PathVariable String email) {
        log.info("Logout attempt for email: {}", email);
        MessageOutDto response = authService.logout(email);
        log.info("Logout successful for email: {}", email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
