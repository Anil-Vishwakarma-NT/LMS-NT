package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.Bundle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BundleTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Bundle bundle = new Bundle();
        bundle.setBundleId(1L);
        bundle.setBundleName("Java Bundle");

        assertEquals(1L, bundle.getBundleId());
        assertEquals("Java Bundle", bundle.getBundleName());
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

        assertEquals(bundle1, bundle2);
        assertEquals(bundle1.hashCode(), bundle2.hashCode());
    }

    @Test
    void testToString() {
        Bundle bundle = new Bundle(4L, "ToString Bundle");
        String expected = "Bundle(bundleId=4, bundleName=ToString Bundle)";
        assertEquals(expected, bundle.toString());
    }
}

