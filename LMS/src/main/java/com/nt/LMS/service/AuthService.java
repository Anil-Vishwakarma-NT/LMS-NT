package com.nt.LMS.service;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.RefreshTokenRepository;
import com.nt.LMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (InvalidRequestException ex) {
            throw new InvalidRequestException(UserConstants.INVALID_CREDENTIALS);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Find user by email
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));

        // Store refresh token in the database
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
//        refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days validity
        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenResponseDto(accessToken, refreshToken, "Bearer");
    }


    public TokenResponseDto  refreshToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (refreshTokenEntity.isPresent() && refreshTokenEntity.get().getExpiryDate().isAfter(Instant.now())) {
            User user = refreshTokenEntity.get().getUser();
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String newAccessToken =  jwtUtil.generateAccessToken(userDetails);

            return new TokenResponseDto(newAccessToken, refreshToken,"Bearer");
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }

    @Transactional
    public MessageOutDto logout(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove refresh token from the database
        refreshTokenRepository.deleteByUser(user);
        return new MessageOutDto(UserConstants.USER_LOGOUT_MESSAGE);
    }
}