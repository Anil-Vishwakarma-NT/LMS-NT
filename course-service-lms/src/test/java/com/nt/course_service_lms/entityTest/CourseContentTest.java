package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.CourseContent;
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
    void testManualSetValuesInsteadOfConstructor() {
        CourseContent cc = new CourseContent();

        cc.setCourseContentId(2L);
        cc.setCourseId(101L);
        cc.setTitle("Module 1");
        cc.setDescription("Module 1 Description");
        cc.setResourceLink("http://resource.com");

        assertThat(cc.getCourseContentId()).isEqualTo(2L);
        assertThat(cc.getCourseId()).isEqualTo(101L);
        assertThat(cc.getTitle()).isEqualTo("Module 1");
        assertThat(cc.getDescription()).isEqualTo("Module 1 Description");
        assertThat(cc.getResourceLink()).isEqualTo("http://resource.com");
        assertThat(cc.isActive()).isFalse();
        assertThat(cc.getCreatedAt()).isNull();
        assertThat(cc.getUpdatedAt()).isNull();
    }

    @Test
    void testEquals_SameValues() {
        CourseContent cc1 = new CourseContent();
        cc1.setCourseContentId(1L);
        cc1.setCourseId(2L);
        cc1.setTitle("Title");
        cc1.setDescription("Desc");
        cc1.setResourceLink("link");

        CourseContent cc2 = new CourseContent();
        cc2.setCourseContentId(1L);
        cc2.setCourseId(2L);
        cc2.setTitle("Title");
        cc2.setDescription("Desc");
        cc2.setResourceLink("link");

        assertThat(cc1).isEqualTo(cc2);
        assertThat(cc1.hashCode()).isEqualTo(cc2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        CourseContent cc1 = new CourseContent();
        cc1.setCourseContentId(1L);
        cc1.setCourseId(2L);
        cc1.setTitle("Title");
        cc1.setDescription("Desc");
        cc1.setResourceLink("link");

        CourseContent cc2 = new CourseContent();
        cc2.setCourseContentId(9L);
        cc2.setCourseId(2L);
        cc2.setTitle("Title");
        cc2.setDescription("Desc");
        cc2.setResourceLink("link");

        CourseContent cc3 = new CourseContent();
        cc3.setCourseContentId(1L);
        cc3.setCourseId(99L);
        cc3.setTitle("Title");
        cc3.setDescription("Desc");
        cc3.setResourceLink("link");

        CourseContent cc4 = new CourseContent();
        cc4.setCourseContentId(1L);
        cc4.setCourseId(2L);
        cc4.setTitle("Different");
        cc4.setDescription("Desc");
        cc4.setResourceLink("link");

        CourseContent cc5 = new CourseContent();
        cc5.setCourseContentId(1L);
        cc5.setCourseId(2L);
        cc5.setTitle("Title");
        cc5.setDescription("Different");
        cc5.setResourceLink("link");

        CourseContent cc6 = new CourseContent();
        cc6.setCourseContentId(1L);
        cc6.setCourseId(2L);
        cc6.setTitle("Title");
        cc6.setDescription("Desc");
        cc6.setResourceLink("different");

        assertThat(cc1).isNotEqualTo(cc2);
        assertThat(cc1).isNotEqualTo(cc3);
        assertThat(cc1).isNotEqualTo(cc4);
        assertThat(cc1).isNotEqualTo(cc5);
        assertThat(cc1).isNotEqualTo(cc6);
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        CourseContent cc = new CourseContent();
        cc.setCourseContentId(1L);
        cc.setCourseId(2L);
        cc.setTitle("Title");
        cc.setDescription("Desc");
        cc.setResourceLink("link");

        assertThat(cc).isNotEqualTo(null);
        assertThat(cc).isNotEqualTo("string");
    }

    @Test
    void testHashCode_DifferentObjects() {
        CourseContent cc1 = new CourseContent();
        cc1.setCourseContentId(1L);
        cc1.setCourseId(2L);
        cc1.setTitle("Title");
        cc1.setDescription("Desc");
        cc1.setResourceLink("link");

        CourseContent cc2 = new CourseContent();
        cc2.setCourseContentId(3L);
        cc2.setCourseId(4L);
        cc2.setTitle("Another");
        cc2.setDescription("Other");
        cc2.setResourceLink("none");

        assertThat(cc1.hashCode()).isNotEqualTo(cc2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_AfterFieldModification() {
        CourseContent cc1 = new CourseContent();
        cc1.setCourseContentId(1L);
        cc1.setCourseId(2L);
        cc1.setTitle("Title");
        cc1.setDescription("Desc");
        cc1.setResourceLink("link");

        CourseContent cc2 = new CourseContent();
        cc2.setCourseContentId(1L);
        cc2.setCourseId(2L);
        cc2.setTitle("Title");
        cc2.setDescription("Desc");
        cc2.setResourceLink("link");

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
