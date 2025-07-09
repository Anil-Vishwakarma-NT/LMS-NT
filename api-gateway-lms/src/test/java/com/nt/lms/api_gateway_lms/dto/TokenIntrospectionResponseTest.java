package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenIntrospectionResponseTest {

    @Test
    void testAllArgsConstructor() {
        TokenIntrospectionResponse response = new TokenIntrospectionResponse(
                true, "sub", "aud", "iss", "access", "read",
                Arrays.asList("ROLE_USER", "ADMIN"),
                "email@example.com", 1000L, 2000L
        );

        assertTrue(response.getActive());
        assertEquals("sub", response.getSubject());
        assertEquals("aud", response.getAudience());
        assertEquals("iss", response.getIssuer());
        assertEquals("access", response.getTokenType());
        assertEquals("read", response.getScope());
        assertEquals(2, response.getRoles().size());
        assertEquals("email@example.com", response.getEmail());
        assertEquals(1000L, response.getIssuedAt());
        assertEquals(2000L, response.getExpiration());
    }

    @Test
    void testBuilderSetsAllFields() {
        TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
                .active(true)
                .subject("dummySubject")
                .audience("dummyAudience")
                .issuer("dummyIssuer")
                .tokenType("Bearer")
                .scope("read write")
                .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
                .email("dummy@nucleusteq.com")
                .issuedAt(1620000000L)
                .expiration(1623600000L)
                .build();

        assertTrue(response.getActive());
        assertEquals("dummySubject", response.getSubject());
        assertEquals("dummyAudience", response.getAudience());
        assertEquals("dummyIssuer", response.getIssuer());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("read write", response.getScope());
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), response.getRoles());
        assertEquals("dummy@nucleusteq.com", response.getEmail());
        assertEquals(1620000000L, response.getIssuedAt());
        assertEquals(1623600000L, response.getExpiration());
    }

    @Test
    void testSettersAndToString() {
        TokenIntrospectionResponse response = new TokenIntrospectionResponse();
        response.setActive(false);
        response.setSubject("subject");
        response.setAudience("audience");
        response.setIssuer("issuer");
        response.setTokenType("refresh");
        response.setScope("write");
        response.setRoles(Collections.singletonList("ROLE_USER"));
        response.setEmail("test@domain.com");
        response.setIssuedAt(1234L);
        response.setExpiration(5678L);

        assertFalse(response.getActive());
        assertEquals("subject", response.getSubject());
        assertTrue(response.toString().contains("subject"));
    }
}
