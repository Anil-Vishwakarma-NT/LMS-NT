package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EncryptResponseTest {

    @Test
    void testConstructorAndGetter() {
        EncryptResponse response = new EncryptResponse("encrypted123");
        assertEquals("encrypted123", response.getEncryptedPassword());
    }

    @Test
    void testEqualsHashCodeToString() {
        EncryptResponse r1 = new EncryptResponse("enc1");
        EncryptResponse r2 = new EncryptResponse("enc1");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("enc1"));
    }
}
