package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DecryptRequestTest {

    @Test
    void testConstructorAndGetters() {
        DecryptRequest request = new DecryptRequest("encrypted", "plain");

        assertEquals("encrypted", request.getEncryptedPassword());
        assertEquals("plain", request.getPlainPassword());
    }

    @Test
    void testSetters() {
        DecryptRequest request = new DecryptRequest(null, null);
        request.setEncryptedPassword("enc");
        request.setPlainPassword("plain");

        assertEquals("enc", request.getEncryptedPassword());
        assertEquals("plain", request.getPlainPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        DecryptRequest r1 = new DecryptRequest("abc", "123");
        DecryptRequest r2 = new DecryptRequest("abc", "123");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        DecryptRequest request = new DecryptRequest("abc", "123");

        String output = request.toString();
        assertTrue(output.contains("abc"));
        assertTrue(output.contains("123"));
    }
}
