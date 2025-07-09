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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {


    private PasswordEncoder passwordEncoder;
    private final Map<String, UserInfo> users;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.users = initializeUsers();
    }

    private Map<String, UserInfo> initializeUsers() {
        Map<String, UserInfo> userMap = new HashMap<>();

        // Sample users - In production, this would come from a database
        userMap.put("admin", new UserInfo("admin", passwordEncoder.encode("password"),
                "anil.vishwakarma@nucleusteq.com", "Anil", "ADMIN"));

        userMap.put("user", new UserInfo("user", passwordEncoder.encode("password"),
                "user@example.com", "Regular User", "ROLE_USER"));

        userMap.put("service", new UserInfo("service", passwordEncoder.encode("servicepass"),
                "service@example.com", "Service Account", "ROLE_SERVICE"));

        return userMap;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Fetch the user by email
        System.out.println(username);
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

    // Method to get additional user information for token generation
    public Mono<UserInfo> getUserInfo(String username) {
        UserInfo userInfo = users.get(username);
        if (userInfo != null) {
            return Mono.just(userInfo);
        }
        return Mono.error(new UsernameNotFoundException("User not found: " + username));
    }

    // Inner class to hold user information
    public static class UserInfo {
        private final String username;
        private final String password;
        private final String email;
        private final String name;
        private final String[] roles;

        public UserInfo(String username, String password, String email, String name, String... roles) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.name = name;
            this.roles = roles;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String[] getRoles() { return roles; }
    }
}