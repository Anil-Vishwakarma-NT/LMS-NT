package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnrollmentsTest {

    private Enrollment enrollment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        enrollment = Enrollment.builder()
                .enrollmentId(1L)
                .userId(101L)
                .groupId(201L)
                .courseId(301L)
                .bundleId(401L)
                .assignedBy(501L)
                .assignedAt(now)
                .deadline(now.plusDays(10))
                .status("active")
                .enrollmentSource("manual")
                .startedAt(now.plusDays(1))
                .completedAt(null)
                .createdAt(now)
                .updatedAt(null)
                .isActive(true)
                .build();
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, enrollment.getEnrollmentId());
        assertEquals(101L, enrollment.getUserId());
        assertEquals(201L, enrollment.getGroupId());
        assertEquals(301L, enrollment.getCourseId());
        assertEquals(401L, enrollment.getBundleId());
        assertEquals(501L, enrollment.getAssignedBy());
        assertEquals(now, enrollment.getAssignedAt());
        assertEquals(now.plusDays(10), enrollment.getDeadline());
        assertEquals("active", enrollment.getStatus());
        assertEquals("manual", enrollment.getEnrollmentSource());
        assertEquals(now.plusDays(1), enrollment.getStartedAt());
        assertNull(enrollment.getCompletedAt());
        assertEquals(now, enrollment.getCreatedAt());
        assertNull(enrollment.getUpdatedAt());
        assertTrue(enrollment.getActive());
    }

    @Test
    void testSetActive() {
        enrollment.setActive(false);
        assertFalse(enrollment.getActive());
    }

    @Test
    void testEqualsAndHashCode_sameObject() {
        Enrollment another = Enrollment.builder()
                .enrollmentId(1L)
                .userId(101L)
                .groupId(201L)
                .courseId(301L)
                .bundleId(401L)
                .assignedBy(501L)
                .assignedAt(now)
                .deadline(now.plusDays(10))
                .status("active")
                .enrollmentSource("manual")
                .startedAt(now.plusDays(1))
                .completedAt(null)
                .createdAt(now)
                .updatedAt(null)
                .isActive(true)
                .build();

        assertThat(enrollment).isEqualTo(another);
        assertThat(enrollment.hashCode()).isEqualTo(another.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentObject() {
        Enrollment different = Enrollment.builder()
                .enrollmentId(2L)
                .userId(102L)
                .assignedBy(502L)
                .enrollmentSource("api")
                .status("completed")
                .assignedAt(now)
                .createdAt(now)
                .isActive(false)
                .build();

        assertThat(enrollment).isNotEqualTo(different);
        assertThat(enrollment.hashCode()).isNotEqualTo(different.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        assertNotEquals(null, enrollment);
        assertNotEquals("some string", enrollment);
        assertEquals(enrollment, enrollment); // reflexivity
    }

    @Test
    void testBuilderDefaults() {
        Enrollment newEnrollment = Enrollment.builder()
                .userId(123L)
                .assignedBy(456L)
                .enrollmentSource("self_enrollment")
                .status("active")
                .createdAt(now)
                .assignedAt(now)
                .isActive(true)
                .build();

        assertEquals("active", newEnrollment.getStatus());
        assertTrue(newEnrollment.getActive());
        assertEquals("self_enrollment", newEnrollment.getEnrollmentSource());
        assertNull(newEnrollment.getCourseId());
        assertNull(newEnrollment.getBundleId());
        assertNull(newEnrollment.getGroupId());
        assertNull(newEnrollment.getStartedAt());
        assertNull(newEnrollment.getCompletedAt());
        assertNull(newEnrollment.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(5L);
        e.setUserId(55L);
        e.setGroupId(65L);
        e.setCourseId(75L);
        e.setBundleId(85L);
        e.setAssignedBy(95L);
        e.setAssignedAt(now);
        e.setDeadline(now.plusDays(5));
        e.setStatus("completed");
        e.setEnrollmentSource("api");
        e.setStartedAt(now.plusDays(2));
        e.setCompletedAt(now.plusDays(3));
        e.setCreatedAt(now);
        e.setUpdatedAt(now.plusDays(4));
        e.setActive(false);

        assertEquals(5L, e.getEnrollmentId());
        assertEquals(55L, e.getUserId());
        assertEquals(65L, e.getGroupId());
        assertEquals(75L, e.getCourseId());
        assertEquals(85L, e.getBundleId());
        assertEquals(95L, e.getAssignedBy());
        assertEquals("completed", e.getStatus());
        assertEquals("api", e.getEnrollmentSource());
        assertFalse(e.getActive());
        assertEquals(now.plusDays(3), e.getCompletedAt());
        assertEquals(now.plusDays(4), e.getUpdatedAt());
    }

    @Test
    void testEqualsWithNullFields() {
        Enrollment e1 = new Enrollment();
        Enrollment e2 = new Enrollment();
        assertEquals(e1, e2); // all fields null/default
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testTransitiveEquals() {
        Enrollment e1 = enrollment;
        Enrollment e2 = Enrollment.builder()
                .enrollmentId(1L)
                .userId(101L)
                .groupId(201L)
                .courseId(301L)
                .bundleId(401L)
                .assignedBy(501L)
                .assignedAt(now)
                .deadline(now.plusDays(10))
                .status("active")
                .enrollmentSource("manual")
                .startedAt(now.plusDays(1))
                .completedAt(null)
                .createdAt(now)
                .updatedAt(null)
                .isActive(true)
                .build();
        Enrollment e3 = Enrollment.builder().build();
        e3.setEnrollmentId(1L);
        e3.setUserId(101L);
        e3.setGroupId(201L);
        e3.setCourseId(301L);
        e3.setBundleId(401L);
        e3.setAssignedBy(501L);
        e3.setAssignedAt(now);
        e3.setDeadline(now.plusDays(10));
        e3.setStatus("active");
        e3.setEnrollmentSource("manual");
        e3.setStartedAt(now.plusDays(1));
        e3.setCompletedAt(null);
        e3.setCreatedAt(now);
        e3.setUpdatedAt(null);
        e3.setActive(true);

        assertEquals(e1, e2);
        assertEquals(e2, e3);
        assertEquals(e1, e3); // transitive
    }

    @Test
    void testDefaultFieldValuesOnInstantiation() {
        Enrollment e = new Enrollment();
        assertNotNull(e.getCreatedAt());
        assertNotNull(e.getAssignedAt());
        assertTrue(e.getActive());
        assertEquals(true, e.getActive());
    }
}
