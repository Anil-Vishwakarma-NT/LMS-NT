package com.nt.LMS.serviceImpl;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.config.RsaDecryptUtil;
import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.StandardResponseOutDTO;
import com.nt.LMS.dto.TokenResponseDto;
import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.RefreshTokenRepository;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static com.nt.LMS.constants.AuthConstants.REFRESH_TOKEN_EXPIRY_SECONDS;

/**
 * Implementation of the AuthService interface.
 * Handles login, logout, and token refreshing.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    /**
     * Used for authenticating user credentials.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Utility for generating and validating JWT tokens.
     */
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    RsaDecryptUtil rsaDecryptUtil;
    /**
     * Loads user details for authentication.
     */
    @Autowired
    private UserServiceImpl userDetailsService;

    /**
     * Repository for performing operations on users.
     */
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;
    /**
     * Repository for managing refresh tokens.
     */
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param loginDto user credentials
     * @return access and refresh tokens
     */
    @Override
    public StandardResponseOutDTO<TokenResponseDto> login(final LoginDto loginDto) {
        log.info("Attempting login for email: {}", loginDto.getEmail());

        if (loginDto.getEmail() == null || loginDto.getPassword() == null) {
            log.warn("Login failed - email or password is null ***************");
            throw new InvalidRequestException(UserConstants.EMAIL_NOT_NULL_MESSAGE);
        }
        try {
//            String decodedPass = new String (Base64.getDecoder().decode(loginDto.getPassword()));

            String decodedPass = rsaDecryptUtil.decrypt(loginDto.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            decodedPass
                    )
            );
        } catch (Exception ex) {
            log.error("Authentication failed for email: {} ***************", loginDto.getEmail());
            throw new InvalidRequestException(UserConstants.INVALID_CREDENTIALS+" "+ex);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        User user = userRepository.findByEmailIgnoreCase(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", loginDto.getEmail());
                    return new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

        if (!user.isActive()) {
            log.warn("Login blocked - user is deactivated: {}", loginDto.getEmail());
            throw new InvalidRequestException(UserConstants.USER_DELETED);
        }

        refreshTokenRepository.deleteByUserId(user.getUserId());
        log.debug("Old refresh token removed for user: {}", user.getEmail());

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getUserId());
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS));
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("Refresh token stored for user: {}", user.getEmail());

        TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken, "Bearer" );
        return StandardResponseOutDTO.success(tokenResponseDto, "User Logged in successfully");
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshToken the refresh token
     * @return new access token and same refresh token
     */
    @Override
    public StandardResponseOutDTO<TokenResponseDto> refreshToken(final String refreshToken) {
        log.info("Refreshing access token using refresh token");

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh token is null or empty");
            throw new InvalidRequestException(UserConstants.REFRESH_TOKEN_CREDENTIALS);
        }

        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (refreshTokenEntity.isEmpty()) {
            log.warn("Refresh token not found in the database");
            throw new InvalidRequestException(UserConstants.REFRESH_TOKEN_CREDENTIALS);
        }

        RefreshToken token = refreshTokenEntity.get();

        if (token.getExpiryDate() != null && token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteById(token.getId());
            log.warn("Refresh token expired and deleted");
            throw new InvalidRequestException(UserConstants.REFRESH_TOKEN_EXPIRE_MASSAGE);
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new InvalidRequestException(UserConstants.USER_NOT_FOUND));

        log.debug("Found valid refresh token for user: {}", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        log.info("New access token generated for user: {}", user.getEmail());

        TokenResponseDto tokenResponseDto = new TokenResponseDto(newAccessToken, refreshToken, "Bearer");
        return StandardResponseOutDTO.success(tokenResponseDto,"New Access Token generated for user");
    }


    /**
     * Logs out a user by deleting their refresh token.
     *
     * @param email the user's email
     * @return success message
     */
    @Override
    @Transactional
    public StandardResponseOutDTO<MessageOutDto> logout(final String email) {
        log.info("Logout request for user: {}", email);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.error("Logout failed - user not found: {}", email);
                    return new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

        refreshTokenRepository.deleteByUserId(user.getUserId());
        log.info("Refresh token deleted for user: {}", email);

        MessageOutDto messageOutDto = new MessageOutDto(UserConstants.USER_LOGOUT_MESSAGE);
        return StandardResponseOutDTO.success(messageOutDto,UserConstants.USER_LOGOUT_MESSAGE);
    }
}
