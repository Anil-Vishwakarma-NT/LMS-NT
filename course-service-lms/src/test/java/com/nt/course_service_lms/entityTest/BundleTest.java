package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.Bundle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BundleTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Bundle bundle = new Bundle();
        LocalDateTime now = LocalDateTime.now();

        bundle.setBundleId(1L);
        bundle.setBundleName("Java Bundle");
        bundle.setActive(true);
        bundle.setCreatedAt(now);
        bundle.setUpdatedAt(now);

        assertEquals(1L, bundle.getBundleId());
        assertEquals("Java Bundle", bundle.getBundleName());
        assertTrue(bundle.isActive());
        assertEquals(now, bundle.getCreatedAt());
        assertEquals(now, bundle.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        Bundle bundle = new Bundle(2L, "Spring Bundle");

        assertEquals(2L, bundle.getBundleId());
        assertEquals("Spring Bundle", bundle.getBundleName());
    }

    @Test
    void testEqualsAndHashCode() {
        Bundle bundle1 = new Bundle(3L, "Test Bundle");
        Bundle bundle2 = new Bundle(3L, "Test Bundle");
        Bundle bundle3 = new Bundle(4L, "Different Bundle");

        assertEquals(bundle1, bundle2);
        assertEquals(bundle1.hashCode(), bundle2.hashCode());
        assertNotEquals(bundle1, bundle3);
        assertNotEquals(bundle1, null);
        assertNotEquals(bundle1, "Some String");
    }

    @Test
    void testToString() {
        Bundle bundle = new Bundle();
        bundle.setBundleId(4L);
        bundle.setBundleName("ToString Bundle");

        String toStringOutput = bundle.toString();
        assertTrue(toStringOutput.contains("bundleId=4"));
        assertTrue(toStringOutput.contains("bundleName=ToString Bundle"));
    }
}
