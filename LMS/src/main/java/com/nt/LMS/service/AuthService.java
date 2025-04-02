package com.nt.LMS.service;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.RefreshTokenRepository;
import com.nt.LMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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

    private final Map<String, String> refreshTokens = new HashMap<>();

    public Map<String, String> login(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Get user and store refresh token in the database
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days validity
        refreshTokenRepository.save(refreshTokenEntity);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String refreshToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (refreshTokenEntity.isPresent() && refreshTokenEntity.get().getExpiryDate().isAfter(Instant.now())) {
            User user = refreshTokenEntity.get().getUser();
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            return jwtUtil.generateAccessToken(userDetails);
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }

    @Transactional
    public void logout(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove refresh token from the database
        refreshTokenRepository.deleteByUser(user);
    }
}