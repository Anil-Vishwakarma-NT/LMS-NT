package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.CourseContent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseContentTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        CourseContent cc = new CourseContent();
        LocalDateTime now = LocalDateTime.now();

        cc.setCourseContentId(1L);
        cc.setCourseId(100L);
        cc.setTitle("Intro");
        cc.setDescription("Introduction to the course");
        cc.setResourceLink("http://example.com");
        cc.setActive(true);
        cc.setCreatedAt(now);
        cc.setUpdatedAt(now);

        assertThat(cc.getCourseContentId()).isEqualTo(1L);
        assertThat(cc.getCourseId()).isEqualTo(100L);
        assertThat(cc.getTitle()).isEqualTo("Intro");
        assertThat(cc.getDescription()).isEqualTo("Introduction to the course");
        assertThat(cc.getResourceLink()).isEqualTo("http://example.com");
        assertThat(cc.isActive()).isTrue();
        assertThat(cc.getCreatedAt()).isEqualTo(now);
        assertThat(cc.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        CourseContent cc = new CourseContent(
                2L,
                101L,
                "Module 1",
                "Module 1 Description",
                "http://resource.com"
        );

        assertThat(cc.getCourseContentId()).isEqualTo(2L);
        assertThat(cc.getCourseId()).isEqualTo(101L);
        assertThat(cc.getTitle()).isEqualTo("Module 1");
        assertThat(cc.getDescription()).isEqualTo("Module 1 Description");
        assertThat(cc.getResourceLink()).isEqualTo("http://resource.com");

        // Check default values
        assertThat(cc.isActive()).isFalse();
        assertThat(cc.getCreatedAt()).isNull();
        assertThat(cc.getUpdatedAt()).isNull();
    }

    @Test
    void testEquals_SameValues() {
        CourseContent cc1 = new CourseContent(1L, 2L, "Title", "Desc", "link");
        CourseContent cc2 = new CourseContent(1L, 2L, "Title", "Desc", "link");

        assertThat(cc1).isEqualTo(cc2);
        assertThat(cc1.hashCode()).isEqualTo(cc2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        CourseContent cc1 = new CourseContent(1L, 2L, "Title", "Desc", "link");
        CourseContent cc2 = new CourseContent(9L, 2L, "Title", "Desc", "link");
        CourseContent cc3 = new CourseContent(1L, 99L, "Title", "Desc", "link");
        CourseContent cc4 = new CourseContent(1L, 2L, "Different", "Desc", "link");
        CourseContent cc5 = new CourseContent(1L, 2L, "Title", "Different", "link");
        CourseContent cc6 = new CourseContent(1L, 2L, "Title", "Desc", "different");

        assertThat(cc1).isNotEqualTo(cc2);
        assertThat(cc1).isNotEqualTo(cc3);
        assertThat(cc1).isNotEqualTo(cc4);
        assertThat(cc1).isNotEqualTo(cc5);
        assertThat(cc1).isNotEqualTo(cc6);
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        CourseContent cc = new CourseContent(1L, 2L, "Title", "Desc", "link");

        assertThat(cc).isNotEqualTo(null);
        assertThat(cc).isNotEqualTo("string");
    }

    @Test
    void testHashCode_DifferentObjects() {
        CourseContent cc1 = new CourseContent(1L, 2L, "Title", "Desc", "link");
        CourseContent cc2 = new CourseContent(3L, 4L, "Another", "Other", "none");

        assertThat(cc1.hashCode()).isNotEqualTo(cc2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_AfterFieldModification() {
        CourseContent cc1 = new CourseContent(1L, 2L, "Title", "Desc", "link");
        CourseContent cc2 = new CourseContent(1L, 2L, "Title", "Desc", "link");

        assertThat(cc1).isEqualTo(cc2);

        cc2.setTitle("Changed");

        assertThat(cc1).isNotEqualTo(cc2);
        assertThat(cc1.hashCode()).isNotEqualTo(cc2.hashCode());
    }

    @Test
    void testToString_ShouldContainFieldValues() {
        CourseContent cc = new CourseContent();
        LocalDateTime now = LocalDateTime.now();

        cc.setCourseContentId(10L);
        cc.setCourseId(20L);
        cc.setTitle("Lesson 1");
        cc.setDescription("Basics");
        cc.setResourceLink("http://link.com");
        cc.setActive(true);
        cc.setCreatedAt(now);
        cc.setUpdatedAt(now);

        String str = cc.toString();

        assertThat(str).contains("courseContentId=10");
        assertThat(str).contains("courseId=20");
        assertThat(str).contains("title=Lesson 1");
        assertThat(str).contains("description=Basics");
        assertThat(str).contains("resourceLink=http://link.com");
        assertThat(str).contains("isActive=true");
        assertThat(str).contains("createdAt=");
        assertThat(str).contains("updatedAt=");
    }
}
