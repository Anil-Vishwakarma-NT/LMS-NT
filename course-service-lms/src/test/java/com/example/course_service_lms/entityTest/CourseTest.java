package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.entity.CourseLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testCourseAllArgsConstructor() {
        Course course = new Course(1L, 101L, "Java Basics", "Intro to Java", "java.png", CourseLevel.BEGINNER);

        assertEquals(1L, course.getCourseId());
        assertEquals(101L, course.getOwnerId());
        assertEquals("Java Basics", course.getTitle());
        assertEquals("Intro to Java", course.getDescription());
        assertEquals("java.png", course.getImage());
        assertEquals(CourseLevel.BEGINNER, course.getLevel());
    }



    @Test
    void testEqualsAndHashCode() {
        Course course1 = new Course(1L, 101L, "Java", "Basics", "java.png", CourseLevel.BEGINNER);
        Course course2 = new Course(1L, 101L, "Java", "Basics", "java.png", CourseLevel.BEGINNER);

        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());

        course2.setTitle("Different Title");
        assertNotEquals(course1, course2);
    }

    @Test
    void testNotEqualToNullOrDifferentType() {
        Course course = new Course(1L, 101L, "Java", "Basics", "java.png", CourseLevel.BEGINNER);

        assertNotEquals(null, course);
        assertNotEquals("some string", course); // different type
    }
}
