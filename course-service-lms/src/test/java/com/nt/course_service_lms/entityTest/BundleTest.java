package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.Bundle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void testFieldAssignmentUsingSetters() {
        Bundle bundle = new Bundle();
        LocalDateTime now = LocalDateTime.now();

        bundle.setBundleId(2L);
        bundle.setBundleName("Spring Bundle");
        bundle.setActive(true);
        bundle.setCreatedAt(now);
        bundle.setUpdatedAt(now);

        assertEquals(2L, bundle.getBundleId());
        assertEquals("Spring Bundle", bundle.getBundleName());
        assertTrue(bundle.isActive());
        assertEquals(now, bundle.getCreatedAt());
        assertEquals(now, bundle.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Bundle bundle1 = Bundle.builder()
                .bundleId(3L)
                .bundleName("Test Bundle")
                .build();

        Bundle bundle2 = Bundle.builder()
                .bundleId(3L)
                .bundleName("Test Bundle")
                .build();

        Bundle bundle3 = Bundle.builder()
                .bundleId(4L)
                .bundleName("Different Bundle")
                .build();

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

    @Test
    void testDefaultValues() {
        Bundle bundle = new Bundle();

        assertEquals(0L, bundle.getBundleId()); // primitive long defaults to 0
        assertNull(bundle.getBundleName());
        assertFalse(bundle.isActive());
        assertNull(bundle.getCreatedAt());
        assertNull(bundle.getUpdatedAt());
    }

    @Test
    void testBuilderCreatesCorrectBundle() {
        LocalDateTime now = LocalDateTime.now();

        Bundle bundle = Bundle.builder()
                .bundleId(10L)
                .bundleName("Builder Bundle")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(10L, bundle.getBundleId());
        assertEquals("Builder Bundle", bundle.getBundleName());
        assertTrue(bundle.isActive());
        assertEquals(now, bundle.getCreatedAt());
        assertEquals(now, bundle.getUpdatedAt());
    }

    @Test
    void testEqualsWithNullOrDifferentName() {
        Bundle bundle1 = Bundle.builder()
                .bundleId(5L)
                .bundleName(null)
                .build();

        Bundle bundle2 = Bundle.builder()
                .bundleId(5L)
                .bundleName(null)
                .build();

        assertEquals(bundle1, bundle2); // both null names, same ID

        Bundle bundle3 = Bundle.builder()
                .bundleId(5L)
                .bundleName("Different")
                .build();

        assertNotEquals(bundle1, bundle3); // one name null, one not
    }

    @Test
    void testHashCodeDifferentForDifferentBundles() {
        Bundle b1 = Bundle.builder().bundleId(1L).bundleName("A").build();
        Bundle b2 = Bundle.builder().bundleId(2L).bundleName("B").build();

        assertNotEquals(b1.hashCode(), b2.hashCode());
    }
}