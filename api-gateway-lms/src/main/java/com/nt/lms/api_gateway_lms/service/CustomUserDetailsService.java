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

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Fetch the user by email
        Users user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the role of the user
        Optional<Role> role = roleRepository.findById(user.getRoleId());

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
        return Mono.error(new UsernameNotFoundException("User not found: " + username));
    }
}