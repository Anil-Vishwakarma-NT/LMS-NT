package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.Course;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    void testAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 6, 1, 12, 0);

        Course course = new Course(
                2L, 20L, "Spring Boot", "Spring Boot in depth",
                "INTERMEDIATE", false, created, updated
        );

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

        Course c1 = new Course(3L, 30L, "Data Structures", "Learn DS", "ADVANCED", true, now, now);
        Course c2 = new Course(3L, 30L, "Data Structures", "Learn DS", "ADVANCED", true, now, now);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_Negative() {
        LocalDateTime now = LocalDateTime.now();

        Course original = new Course(4L, 40L, "Algorithms", "Learn Algorithms", "ADVANCED", true, now, now);

        // Different ID
        Course diffId = new Course(5L, 40L, "Algorithms", "Learn Algorithms", "ADVANCED", true, now, now);
        assertNotEquals(original, diffId);

        // Different Owner
        Course diffOwner = new Course(4L, 41L, "Algorithms", "Learn Algorithms", "ADVANCED", true, now, now);
        assertNotEquals(original, diffOwner);

        // Different title
        Course diffTitle = new Course(4L, 40L, "Different Title", "Learn Algorithms", "ADVANCED", true, now, now);
        assertNotEquals(original, diffTitle);

        // Different description
        Course diffDesc = new Course(4L, 40L, "Algorithms", "Different Desc", "ADVANCED", true, now, now);
        assertNotEquals(original, diffDesc);

        // Different level
        Course diffLevel = new Course(4L, 40L, "Algorithms", "Learn Algorithms", "BEGINNER", true, now, now);
        assertNotEquals(original, diffLevel);

        // Different isActive
        Course diffActive = new Course(4L, 40L, "Algorithms", "Learn Algorithms", "ADVANCED", false, now, now);
        assertNotEquals(original, diffActive);

        // Different createdAt
        Course diffCreated = new Course(4L, 40L, "Algorithms", "Learn Algorithms", "ADVANCED", true, now.minusDays(1), now);
        assertNotEquals(original, diffCreated);

        // Different updatedAt
        Course diffUpdated = new Course(4L, 40L, "Algorithms", "Learn Algorithms", "ADVANCED", true, now, now.plusDays(1));
        assertNotEquals(original, diffUpdated);

        // Null and different type
        assertNotEquals(original, null);
        assertNotEquals(original, "Some String");
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();
        Course course = new Course(6L, 60L, "AI Course", "Intro to AI", "BEGINNER", true, now, now);

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
