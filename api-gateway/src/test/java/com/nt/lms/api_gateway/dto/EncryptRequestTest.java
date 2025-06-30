package com.nt.lms.api_gateway.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptRequestTest {

    @Test
    void testConstructorAndGetter() {
        EncryptRequest request = new EncryptRequest("my-password");
        assertEquals("my-password", request.getPassword());
    }

    @Test
    void testEqualsHashCodeToString() {
        EncryptRequest r1 = new EncryptRequest("secret");
        EncryptRequest r2 = new EncryptRequest("secret");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("secret"));
    }
}
