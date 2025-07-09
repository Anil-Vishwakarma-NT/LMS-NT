package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTokenResponseTest {

    @Test
    void testAllArgsConstructorAndGetter() {
        ServiceTokenResponse response = new ServiceTokenResponse("abc.def.ghi");
        assertEquals("abc.def.ghi", response.getServiceToken());
    }

    @Test
    void testSetter() {
        ServiceTokenResponse response = new ServiceTokenResponse();
        response.setServiceToken("xyz.token.value");
        assertEquals("xyz.token.value", response.getServiceToken());
    }

    @Test
    void testEqualsHashCodeToString() {
        ServiceTokenResponse r1 = new ServiceTokenResponse("token123");
        ServiceTokenResponse r2 = new ServiceTokenResponse("token123");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("token123"));
    }
}
