package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.Course;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Course course = new Course();
        LocalDateTime now = LocalDateTime.now();

        course.setCourseId(1L);
        course.setOwnerId(10L);
        course.setTitle("Java Basics");
        course.setDescription("Learn Java from scratch");
        course.setLevel("BEGINNER");
        course.setActive(true);
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        assertEquals(1L, course.getCourseId());
        assertEquals(10L, course.getOwnerId());
        assertEquals("Java Basics", course.getTitle());
        assertEquals("Learn Java from scratch", course.getDescription());
        assertEquals("BEGINNER", course.getLevel());
        assertTrue(course.isActive());
        assertEquals(now, course.getCreatedAt());
        assertEquals(now, course.getUpdatedAt());
    }

    @Test
    void testManualSettersInsteadOfAllArgsConstructor() {
        Course course = new Course();
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 6, 1, 12, 0);

        course.setCourseId(2L);
        course.setOwnerId(20L);
        course.setTitle("Spring Boot");
        course.setDescription("Spring Boot in depth");
        course.setLevel("INTERMEDIATE");
        course.setActive(false);
        course.setCreatedAt(created);
        course.setUpdatedAt(updated);

        assertEquals(2L, course.getCourseId());
        assertEquals(20L, course.getOwnerId());
        assertEquals("Spring Boot", course.getTitle());
        assertEquals("Spring Boot in depth", course.getDescription());
        assertEquals("INTERMEDIATE", course.getLevel());
        assertFalse(course.isActive());
        assertEquals(created, course.getCreatedAt());
        assertEquals(updated, course.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode_Positive() {
        LocalDateTime now = LocalDateTime.now();

        Course c1 = new Course();
        c1.setCourseId(3L);
        c1.setOwnerId(30L);
        c1.setTitle("Data Structures");
        c1.setDescription("Learn DS");
        c1.setLevel("ADVANCED");
        c1.setActive(true);
        c1.setCreatedAt(now);
        c1.setUpdatedAt(now);

        Course c2 = new Course();
        c2.setCourseId(3L);
        c2.setOwnerId(30L);
        c2.setTitle("Data Structures");
        c2.setDescription("Learn DS");
        c2.setLevel("ADVANCED");
        c2.setActive(true);
        c2.setCreatedAt(now);
        c2.setUpdatedAt(now);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testDefaultFieldValuesAfterNoArgsConstructor() {
        Course course = new Course();

        assertEquals(0L, course.getCourseId());
        assertEquals(0L, course.getOwnerId());
        assertNull(course.getTitle());
        assertNull(course.getDescription());
        assertNull(course.getLevel());
        assertFalse(course.isActive()); // default for boolean
        assertNull(course.getCreatedAt());
        assertNull(course.getUpdatedAt());
    }

    @Test
    void testBuilderCreatesCorrectObject() {
        LocalDateTime now = LocalDateTime.now();

        Course course = Course.builder()
                .courseId(7L)
                .ownerId(70L)
                .title("Builder Course")
                .description("Using Lombok Builder")
                .level("INTERMEDIATE")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(7L, course.getCourseId());
        assertEquals(70L, course.getOwnerId());
        assertEquals("Builder Course", course.getTitle());
        assertEquals("Using Lombok Builder", course.getDescription());
        assertEquals("INTERMEDIATE", course.getLevel());
        assertTrue(course.isActive());
        assertEquals(now, course.getCreatedAt());
        assertEquals(now, course.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode_Negative() {
        LocalDateTime now = LocalDateTime.now();

        Course original = new Course();
        original.setCourseId(4L);
        original.setOwnerId(40L);
        original.setTitle("Algorithms");
        original.setDescription("Learn Algorithms");
        original.setLevel("ADVANCED");
        original.setActive(true);
        original.setCreatedAt(now);
        original.setUpdatedAt(now);

        Course diffId = new Course();
        diffId.setCourseId(5L); // different
        diffId.setOwnerId(40L);
        diffId.setTitle("Algorithms");
        diffId.setDescription("Learn Algorithms");
        diffId.setLevel("ADVANCED");
        diffId.setActive(true);
        diffId.setCreatedAt(now);
        diffId.setUpdatedAt(now);
        assertNotEquals(original, diffId);

        Course diffOwner = new Course();
        diffOwner.setCourseId(4L);
        diffOwner.setOwnerId(41L); // different
        diffOwner.setTitle("Algorithms");
        diffOwner.setDescription("Learn Algorithms");
        diffOwner.setLevel("ADVANCED");
        diffOwner.setActive(true);
        diffOwner.setCreatedAt(now);
        diffOwner.setUpdatedAt(now);
        assertNotEquals(original, diffOwner);

        Course diffTitle = new Course();
        diffTitle.setCourseId(4L);
        diffTitle.setOwnerId(40L);
        diffTitle.setTitle("Different Title"); // different
        diffTitle.setDescription("Learn Algorithms");
        diffTitle.setLevel("ADVANCED");
        diffTitle.setActive(true);
        diffTitle.setCreatedAt(now);
        diffTitle.setUpdatedAt(now);
        assertNotEquals(original, diffTitle);

        Course diffDesc = new Course();
        diffDesc.setCourseId(4L);
        diffDesc.setOwnerId(40L);
        diffDesc.setTitle("Algorithms");
        diffDesc.setDescription("Different Desc"); // different
        diffDesc.setLevel("ADVANCED");
        diffDesc.setActive(true);
        diffDesc.setCreatedAt(now);
        diffDesc.setUpdatedAt(now);
        assertNotEquals(original, diffDesc);

        Course diffLevel = new Course();
        diffLevel.setCourseId(4L);
        diffLevel.setOwnerId(40L);
        diffLevel.setTitle("Algorithms");
        diffLevel.setDescription("Learn Algorithms");
        diffLevel.setLevel("BEGINNER"); // different
        diffLevel.setActive(true);
        diffLevel.setCreatedAt(now);
        diffLevel.setUpdatedAt(now);
        assertNotEquals(original, diffLevel);

        Course diffActive = new Course();
        diffActive.setCourseId(4L);
        diffActive.setOwnerId(40L);
        diffActive.setTitle("Algorithms");
        diffActive.setDescription("Learn Algorithms");
        diffActive.setLevel("ADVANCED");
        diffActive.setActive(false); // different
        diffActive.setCreatedAt(now);
        diffActive.setUpdatedAt(now);
        assertNotEquals(original, diffActive);

        Course diffCreated = new Course();
        diffCreated.setCourseId(4L);
        diffCreated.setOwnerId(40L);
        diffCreated.setTitle("Algorithms");
        diffCreated.setDescription("Learn Algorithms");
        diffCreated.setLevel("ADVANCED");
        diffCreated.setActive(true);
        diffCreated.setCreatedAt(now.minusDays(1)); // different
        diffCreated.setUpdatedAt(now);
        assertNotEquals(original, diffCreated);

        Course diffUpdated = new Course();
        diffUpdated.setCourseId(4L);
        diffUpdated.setOwnerId(40L);
        diffUpdated.setTitle("Algorithms");
        diffUpdated.setDescription("Learn Algorithms");
        diffUpdated.setLevel("ADVANCED");
        diffUpdated.setActive(true);
        diffUpdated.setCreatedAt(now);
        diffUpdated.setUpdatedAt(now.plusDays(1)); // different
        assertNotEquals(original, diffUpdated);

        assertNotEquals(original, null);
        assertNotEquals(original, "Some String");
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();
        Course course = new Course();

        course.setCourseId(6L);
        course.setOwnerId(60L);
        course.setTitle("AI Course");
        course.setDescription("Intro to AI");
        course.setLevel("BEGINNER");
        course.setActive(true);
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        String toString = course.toString();
        assertTrue(toString.contains("courseId=6"));
        assertTrue(toString.contains("ownerId=60"));
        assertTrue(toString.contains("title=AI Course"));
        assertTrue(toString.contains("description=Intro to AI"));
        assertTrue(toString.contains("level=BEGINNER"));
        assertTrue(toString.contains("isActive=true"));
        assertTrue(toString.contains("createdAt="));
        assertTrue(toString.contains("updatedAt="));
    }
}
