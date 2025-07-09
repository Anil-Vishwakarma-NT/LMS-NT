package com.nt.lms.api_gateway_lms.service;

import com.nt.lms.api_gateway_lms.dto.CustomUserDetails;
import com.nt.lms.api_gateway_lms.entities.Role;
import com.nt.lms.api_gateway_lms.entities.Users;
import com.nt.lms.api_gateway_lms.repository.RoleRepository;
import com.nt.lms.api_gateway_lms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Users dummyUser;
    private Role dummyRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dummyUser = new Users(
                1L,
                "dummyuser",
                "Dummy",
                "User",
                "dummy@nucleusteq.com",
                "encodedPass",
                1L,
                1L,
                new Date(),
                new Date(),
                true
        );

        dummyRole = new Role();
        dummyRole.setRoleId(1L);
        dummyRole.setName("ROLE_USER");

        customUserDetailsService = new CustomUserDetailsService(passwordEncoder);

        customUserDetailsService.userRepository = userRepository;
        customUserDetailsService.roleRepository = roleRepository;
    }

    @Test
    void testFindByUsername_UserExistsAndRoleExists() {
        Users mockUser = new Users(
                2L, "dummyUser", "Dummy", "User", "dummy@nucleusteq.com", "encodedPass",
                1L, 1L, new Date(), new Date(), true
        );

        Role mockRole = new Role();
        mockRole.setRoleId(1L);
        mockRole.setName("ADMIN");

        when(userRepository.findByEmailIgnoreCase("dummy@nucleusteq.com")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));

        Mono<UserDetails> result = customUserDetailsService.findByUsername("dummy@nucleusteq.com");

        StepVerifier.create(result)
                .assertNext(userDetails -> {
                    CustomUserDetails customUser = (CustomUserDetails) userDetails;
                    assert customUser.getEmail().equals("dummy@nucleusteq.com");
                    assert customUser.getFullName().equals("Dummy User");
                    assert customUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void testFindByUsername_UserExistsButRoleMissing() {
        Users mockUser = new Users(
                2L, "dummyUser", "Dummy", "User", "dummy@nucleusteq.com", "encodedPass",
                1L, 1L, new Date(), new Date(), true
        );

        when(userRepository.findByEmailIgnoreCase("dummy@nucleusteq.com")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Mono<UserDetails> result = customUserDetailsService.findByUsername("dummy@nucleusteq.com");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("User not found"))
                .verify();
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByEmailIgnoreCase("notfound@nucleusteq.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> customUserDetailsService.findByUsername("notfound@nucleusteq.com").block());
    }

    @Test
    void testGetUserInfo_UserExistsInMemory() {
        CustomUserDetailsService realService = new CustomUserDetailsService(passwordEncoder);
        Mono<CustomUserDetailsService.UserInfo> userInfoMono = realService.getUserInfo("admin");

        StepVerifier.create(userInfoMono)
                .assertNext(info -> assertEquals("admin", info.getUsername()))
                .verifyComplete();
    }

    @Test
    void testGetUserInfo_UserNotFoundInMemory() {
        CustomUserDetailsService realService = new CustomUserDetailsService(passwordEncoder);

        StepVerifier.create(realService.getUserInfo("unknown"))
                .expectError(UsernameNotFoundException.class)
                .verify();
    }

    @Test
    void testUserInfoGetters() {
        CustomUserDetailsService.UserInfo userInfo = new CustomUserDetailsService.UserInfo(
                "dummyuser",
                "dummyPass",
                "dummy@nucleusteq.com",
                "Dummy User",
                "ROLE_USER", "ROLE_ADMIN"
        );

        assertEquals("dummyuser", userInfo.getUsername());
        assertEquals("dummyPass", userInfo.getPassword());
        assertEquals("dummy@nucleusteq.com", userInfo.getEmail());
        assertEquals("Dummy User", userInfo.getName());
        assertArrayEquals(new String[]{"ROLE_USER", "ROLE_ADMIN"}, userInfo.getRoles());
    }

}
