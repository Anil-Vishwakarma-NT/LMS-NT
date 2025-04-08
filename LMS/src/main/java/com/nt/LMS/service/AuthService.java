package com.nt.LMS.service;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.config.SecurityConfig;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.RefreshTokenRepository;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashSet;
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

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, String> refreshTokens = new HashMap<>();


    public String register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findById(registerDto.getRoleId()).orElseThrow(()-> new RuntimeException("Role does not exist"));

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUserName(registerDto.getUserName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(role);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
//        user.setGroups(new HashSet<>());

        userRepository.save(user);
        return "User registered successfully";
    }

//    public String login(LoginDto loginDto) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
//        );
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//        return jwtUtil.generateToken(userDetails);
//    }



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
