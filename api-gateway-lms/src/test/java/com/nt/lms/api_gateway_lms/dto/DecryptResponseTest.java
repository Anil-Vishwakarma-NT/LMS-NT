package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecryptResponseTest {

    @Test
    void testAllArgsConstructorAndGetter() {
        DecryptResponse response = new DecryptResponse(true);
        assertTrue(response.isPasswordMatched());
    }

    @Test
    void testEqualsHashCodeToString() {
        DecryptResponse r1 = new DecryptResponse(true);
        DecryptResponse r2 = new DecryptResponse(true);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("passwordMatched"));
    }
}
