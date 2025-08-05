package com.nt.lms.api_gateway_lms.service;

import com.nt.lms.api_gateway_lms.dto.CustomUserDetails;
import com.nt.lms.api_gateway_lms.entities.Role;
import com.nt.lms.api_gateway_lms.entities.Users;
import com.nt.lms.api_gateway_lms.repository.RoleRepository;
import com.nt.lms.api_gateway_lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of {@link ReactiveUserDetailsService} for Spring Security.
 * <p>
 * This service is responsible for loading user-specific data (like credentials and roles)
 * during the authentication process using reactive programming with {@link Mono}.
 * </p>
 */
@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    /**
     * Repository for accessing user-related data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for accessing role-related data.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Finds the user details by username (email) for authentication.
     *
     * @param username the email of the user attempting to authenticate
     * @return a {@link Mono} emitting the {@link UserDetails} of the user if found,
     * or emitting an error if the user is not found
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        // Fetch the user by email (case-insensitive)
        Users user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the role associated with the user
        Optional<Role> role = roleRepository.findById(user.getRoleId());

        // If user and role are present, create and return CustomUserDetails
        if (user != null && role.isPresent()) {
            return Mono.just(
                    new CustomUserDetails(
                            String.valueOf(user.getUserId()),
                            user.getEmail(),
                            user.getPassword(),
                            user.getFirstName() + " " + user.getLastName(),
                            user.isActive(),
                            List.of(new SimpleGrantedAuthority(role.get().getName()))
                    )
            );
        }

        // If user or role is not found, emit an error
        return Mono.error(new UsernameNotFoundException("User not found: " + username));
    }
}
