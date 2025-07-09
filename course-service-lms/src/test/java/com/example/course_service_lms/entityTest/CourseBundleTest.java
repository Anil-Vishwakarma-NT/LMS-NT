package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.CourseBundle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseBundleTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Given
        CourseBundle cb = new CourseBundle();
        LocalDateTime now = LocalDateTime.now();

        // When
        cb.setCourseBundleId(1L);
        cb.setBundleId(2L);
        cb.setCourseId(3L);
        cb.setActive(true);
        cb.setCreatedAt(now);
        cb.setUpdatedAt(now);

        // Then
        assertThat(cb.getCourseBundleId()).isEqualTo(1L);
        assertThat(cb.getBundleId()).isEqualTo(2L);
        assertThat(cb.getCourseId()).isEqualTo(3L);
        assertThat(cb.isActive()).isTrue();
        assertThat(cb.getCreatedAt()).isEqualTo(now);
        assertThat(cb.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructorOnlySetsThreeFields() {
        // Given
        CourseBundle cb = new CourseBundle(10L, 20L, 30L);

        // Then
        assertThat(cb.getCourseBundleId()).isEqualTo(10L);
        assertThat(cb.getBundleId()).isEqualTo(20L);
        assertThat(cb.getCourseId()).isEqualTo(30L);
        assertThat(cb.isActive()).isFalse(); // Default
        assertThat(cb.getCreatedAt()).isNull();
        assertThat(cb.getUpdatedAt()).isNull();
    }

    @Test
    void testEquals_SameValues() {
        // Given
        CourseBundle cb1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle cb2 = new CourseBundle(1L, 2L, 3L);

        // Then
        assertThat(cb1).isEqualTo(cb2);
        assertThat(cb1.hashCode()).isEqualTo(cb2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        CourseBundle cb1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle cb2 = new CourseBundle(9L, 2L, 3L);
        CourseBundle cb3 = new CourseBundle(1L, 9L, 3L);
        CourseBundle cb4 = new CourseBundle(1L, 2L, 9L);

        assertThat(cb1).isNotEqualTo(cb2);
        assertThat(cb1).isNotEqualTo(cb3);
        assertThat(cb1).isNotEqualTo(cb4);
    }

    @Test
    void testEquals_OtherClassAndNull() {
        CourseBundle cb = new CourseBundle(1L, 2L, 3L);

        assertThat(cb).isNotEqualTo(null);
        assertThat(cb).isNotEqualTo("some string");
    }

    @Test
    void testHashCode_DifferentObjects() {
        CourseBundle cb1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle cb2 = new CourseBundle(4L, 5L, 6L);

        assertThat(cb1.hashCode()).isNotEqualTo(cb2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeAfterFieldModification() {
        CourseBundle cb1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle cb2 = new CourseBundle(1L, 2L, 3L);

        assertThat(cb1).isEqualTo(cb2);
        assertThat(cb1.hashCode()).isEqualTo(cb2.hashCode());

        cb2.setCourseId(99L);

        assertThat(cb1).isNotEqualTo(cb2);
        assertThat(cb1.hashCode()).isNotEqualTo(cb2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();
        CourseBundle cb = new CourseBundle();
        cb.setCourseBundleId(1L);
        cb.setBundleId(2L);
        cb.setCourseId(3L);
        cb.setActive(true);
        cb.setCreatedAt(now);
        cb.setUpdatedAt(now);

        String result = cb.toString();

        assertThat(result).contains("courseBundleId=1");
        assertThat(result).contains("bundleId=2");
        assertThat(result).contains("courseId=3");
        assertThat(result).contains("isActive=true");
        assertThat(result).contains("createdAt=");
        assertThat(result).contains("updatedAt=");
    }
}
