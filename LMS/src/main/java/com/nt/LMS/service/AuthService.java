package com.nt.LMS.service;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.RegisterDto;

import com.nt.LMS.constants.UserConstants;

import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;

import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.RefreshTokenRepository;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public TokenResponseDto login(LoginDto loginDto) {
        log.info("Attempting login for email: {}", loginDto.getEmail());

        if (loginDto.getEmail() == null || loginDto.getPassword() == null) {
            log.warn("Login failed - email or password is null");
            throw new InvalidRequestException(UserConstants.EMAIL_NOT_NULL_MESSAGE);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (Exception ex) {
            log.error("Authentication failed for email: {}", loginDto.getEmail());
            throw new InvalidRequestException(UserConstants.INVALID_CREDENTIALS);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", loginDto.getEmail());
                    return new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

        // Remove any existing refresh tokens
        refreshTokenRepository.deleteByUser(user);
        log.debug("Old refresh token removed for user: {}", user.getEmail());

        // Store new refresh token
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days validity
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("Refresh token stored for user: {}", user.getEmail());

        return new TokenResponseDto(accessToken, refreshToken, "Bearer");
    }

    public TokenResponseDto refreshToken(String refreshToken) {
        log.info("Refreshing access token using refresh token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh token is null or empty");
            throw new InvalidRequestException(UserConstants.REFRESH_TOKEN_CREDENTIALS);
        }

        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (refreshTokenEntity.isPresent() &&
                refreshTokenEntity.get().getExpiryDate() != null &&
                refreshTokenEntity.get().getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepository.deleteById(refreshTokenEntity.get().getId());
            log.warn("Refresh token expired and deleted");
            throw new InvalidRequestException(UserConstants.REFRESH_TOKEN_EXPIRE_MASSAGE);
        }

        User user = refreshTokenEntity.get().getUser();
        log.debug("Found valid refresh token for user: {}", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        log.info("New access token generated for user: {}", user.getEmail());

        return new TokenResponseDto(newAccessToken, refreshToken, "Bearer");
    }

    @Transactional
    public MessageOutDto logout(String email) {
        log.info("Logout request for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Logout failed - user not found: {}", email);
                    return new RuntimeException("User not found");
                });

        refreshTokenRepository.deleteByUser(user);
        log.info("Refresh token deleted for user: {}", email);

        return new MessageOutDto(UserConstants.USER_LOGOUT_MESSAGE);
    }
}
