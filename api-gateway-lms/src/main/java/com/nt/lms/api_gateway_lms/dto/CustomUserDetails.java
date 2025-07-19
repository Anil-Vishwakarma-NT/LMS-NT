package com.nt.lms.api_gateway_lms.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Custom implementation of {@link UserDetails} for Spring Security.
 * <p>
 * This class represents the authenticated user's details such as user ID, email,
 * password, full name, and roles/authorities. It is used by Spring Security during
 * the authentication and authorization process.
 * </p>
 */
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * Unique identifier of the user.
     */
    private String userId;

    /**
     * Email of the user, used as the username for authentication.
     */
    private String email;

    /**
     * Encrypted password of the user.
     */
    private String password;

    /**
     * Full name of the user.
     */
    private String fullName;

    /**
     * Indicates whether the user account is active.
     */
    private Boolean active;

    /**
     * Collection of authorities (roles/permissions) granted to the user.
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the email of the user.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the collection of authorities granted to the user.
     *
     * @return the authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the encrypted password of the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used for authentication, which in this case is the email.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return email; // or return userId;
    }

    /**
     * Returns the full name of the user.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Indicates whether the user account is active.
     *
     * @return {@code true} if the user is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Indicates whether the user's account has expired.
     * Always returns {@code true} as this field is not managed.
     *
     * @return {@code true}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * Always returns {@code true} as this field is not managed.
     *
     * @return {@code true}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * Always returns {@code true} as this field is not managed.
     *
     * @return {@code true}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * Delegates to the {@code active} field.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    @Override
    public boolean isEnabled() {
        return active;
    }
}
