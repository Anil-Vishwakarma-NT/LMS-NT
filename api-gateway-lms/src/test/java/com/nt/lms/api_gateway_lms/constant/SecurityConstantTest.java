package com.nt.lms.api_gateway_lms.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityConstantTest {

    @Test
    void testConstantValues() {
        assertEquals("Bearer ", SecurityConstant.BEARER_PREFIX);
        assertEquals("access", SecurityConstant.ACCESS_TOKEN_TYPE);
        assertEquals("service", SecurityConstant.SERVICE_TOKEN_TYPE);
        assertEquals("refresh", SecurityConstant.REFRESH_TOKEN_TYPE);

        assertEquals("scope", SecurityConstant.CLAIM_SCOPE);
        assertEquals("roles", SecurityConstant.CLAIM_ROLES);
        assertEquals("email", SecurityConstant.CLAIM_EMAIL);

        assertEquals("X-Service-Token", SecurityConstant.SERVICE_TOKEN_HEADER);
        assertEquals("api-gateway", SecurityConstant.GATEWAY_SOURCE_VALUE);

        assertEquals("JWT token has expired", SecurityConstant.TOKEN_EXPIRED_MSG);
        assertEquals("user-service", SecurityConstant.USER_SERVICE);

        assertEquals("https://auth.nucleusteq.com", SecurityConstant.DEFAULT_JWT_ISSUER);
        assertEquals(900000, SecurityConstant.DEFAULT_ACCESS_TOKEN_EXPIRATION);
        assertEquals(604800000, SecurityConstant.DEFAULT_REFRESH_TOKEN_EXPIRATION);
        assertEquals(24 * 60 * 60 * 1000L, SecurityConstant.REFRESH_WINDOW);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<SecurityConstant> constructor = SecurityConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> constructor.newInstance());
    }
}

