package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.CourseContent;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CourseContentTest {

    @Test
    void testAllArgsConstructor() {
        // Given
        CourseContent courseContent = new CourseContent(1L, 101L, "Java Basics", "Introduction to Java", "http://video.com", "http://resource.com");

        // Then
        assertThat(courseContent.getCourseContentId()).isEqualTo(1L);
        assertThat(courseContent.getCourseId()).isEqualTo(101L);
        assertThat(courseContent.getTitle()).isEqualTo("Java Basics");
        assertThat(courseContent.getDescription()).isEqualTo("Introduction to Java");
        assertThat(courseContent.getVideoLink()).isEqualTo("http://video.com");
        assertThat(courseContent.getResourceLink()).isEqualTo("http://resource.com");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        CourseContent courseContent = new CourseContent();

        // Then
        assertThat(courseContent).isNotNull();
        assertThat(courseContent.getCourseContentId()).isEqualTo(0L);
        assertThat(courseContent.getCourseId()).isEqualTo(0L);
        assertThat(courseContent.getTitle()).isNull();
        assertThat(courseContent.getDescription()).isNull();
        assertThat(courseContent.getVideoLink()).isNull();
        assertThat(courseContent.getResourceLink()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseContentId(1L);
        courseContent.setCourseId(101L);
        courseContent.setTitle("Java Basics");
        courseContent.setDescription("Introduction to Java");
        courseContent.setVideoLink("http://video.com");
        courseContent.setResourceLink("http://resource.com");

        // Then
        assertThat(courseContent.getCourseContentId()).isEqualTo(1L);
        assertThat(courseContent.getCourseId()).isEqualTo(101L);
        assertThat(courseContent.getTitle()).isEqualTo("Java Basics");
        assertThat(courseContent.getDescription()).isEqualTo("Introduction to Java");
        assertThat(courseContent.getVideoLink()).isEqualTo("http://video.com");
        assertThat(courseContent.getResourceLink()).isEqualTo("http://resource.com");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CourseContent content1 = new CourseContent(1L, 101L, "Title1", "Description1", "VideoLink1", "ResourceLink1");
        CourseContent content2 = new CourseContent(1L, 101L, "Title1", "Description1", "VideoLink1", "ResourceLink1");

        // Then
        assertThat(content1).isEqualTo(content2);
        assertThat(content1.hashCode()).isEqualTo(content2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeAfterModification() {
        // Given
        CourseContent content1 = new CourseContent(1L, 101L, "Title1", "Description1", "VideoLink1", "ResourceLink1");
        CourseContent content2 = new CourseContent(1L, 101L, "Title1", "Description1", "VideoLink1", "ResourceLink1");

        // Modify content2
        content2.setTitle("Modified Title");

        // Then
        assertThat(content1).isNotEqualTo(content2);
        assertThat(content1.hashCode()).isNotEqualTo(content2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        CourseContent courseContent = new CourseContent(1L, 101L, "Java Basics", "Introduction to Java", "http://video.com", "http://resource.com");

        // Then
        assertThat(courseContent.toString())
                .contains("courseContentId=1")
                .contains("courseId=101")
                .contains("title=Java Basics")
                .contains("description=Introduction to Java")
                .contains("videoLink=http://video.com")
                .contains("resourceLink=http://resource.com");
    }
}