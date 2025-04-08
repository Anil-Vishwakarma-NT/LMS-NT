package com.nt.LMS.controller;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginDto loginDto) {
        TokenResponseDto response = authService.login(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        TokenResponseDto response = authService.refreshToken(request.get("refreshToken"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout/{email}")
    public ResponseEntity<MessageOutDto> logout(@PathVariable String email) {
        MessageOutDto response =authService.logout(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
