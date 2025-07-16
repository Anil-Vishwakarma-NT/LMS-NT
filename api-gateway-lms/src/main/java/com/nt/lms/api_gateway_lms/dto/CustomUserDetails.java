package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String userId;
    private String email;
    private String password;
    private String fullName;
    private Boolean active;
    private Collection<? extends GrantedAuthority> authorities;


    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // or userId.toString()
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isActive() {
        return active;
    }
    // Other methods like isAccountNonExpired, etc.
}

