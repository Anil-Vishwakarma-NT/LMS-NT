package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTokenRequestTest {

    @Test
    void testAllArgsConstructorAndGetter() {
        ServiceTokenRequest request = new ServiceTokenRequest("dummy-service");
        assertEquals("dummy-service", request.getTargetService());
    }

    @Test
    void testSetter() {
        ServiceTokenRequest request = new ServiceTokenRequest();
        request.setTargetService("service-name");
        assertEquals("service-name", request.getTargetService());
    }

    @Test
    void testEqualsHashCodeToString() {
        ServiceTokenRequest r1 = new ServiceTokenRequest("service");
        ServiceTokenRequest r2 = new ServiceTokenRequest("service");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("service"));
    }
}
