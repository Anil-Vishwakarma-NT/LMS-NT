package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.CourseBundle;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CourseBundleTest {

    @Test
    void testConstructorInitialization() {
        // Given
        long courseBundleId = 1L;
        long bundleId = 2L;
        long courseId = 3L;

        // When
        CourseBundle courseBundle = new CourseBundle(courseBundleId, bundleId, courseId);

        // Then
        assertThat(courseBundle.getCourseBundleId()).isEqualTo(courseBundleId);
        assertThat(courseBundle.getBundleId()).isEqualTo(bundleId);
        assertThat(courseBundle.getCourseId()).isEqualTo(courseId);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        CourseBundle courseBundle = new CourseBundle();
        long courseBundleId = 5L;
        long bundleId = 10L;
        long courseId = 15L;

        // When
        courseBundle.setCourseBundleId(courseBundleId);
        courseBundle.setBundleId(bundleId);
        courseBundle.setCourseId(courseId);

        // Then
        assertThat(courseBundle.getCourseBundleId()).isEqualTo(courseBundleId);
        assertThat(courseBundle.getBundleId()).isEqualTo(bundleId);
        assertThat(courseBundle.getCourseId()).isEqualTo(courseId);
    }

    @Test
    void testEquals() {
        // Given
        CourseBundle courseBundle1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle courseBundle2 = new CourseBundle(1L, 2L, 3L);
        CourseBundle courseBundle3 = new CourseBundle(4L, 5L, 6L);

        // Then
        assertThat(courseBundle1).isEqualTo(courseBundle2); // Equal objects
        assertThat(courseBundle1).isNotEqualTo(courseBundle3); // Non-equal objects
        assertThat(courseBundle1).isNotEqualTo(null); // Not equal to null
    }

    @Test
    void testHashCode() {
        // Given
        CourseBundle courseBundle1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle courseBundle2 = new CourseBundle(1L, 2L, 3L);
        CourseBundle courseBundle3 = new CourseBundle(4L, 5L, 6L);

        // Then
        assertThat(courseBundle1.hashCode()).isEqualTo(courseBundle2.hashCode()); // Hash codes match
        assertThat(courseBundle1.hashCode()).isNotEqualTo(courseBundle3.hashCode()); // Hash codes differ
    }

    @Test
    void testNoArgsConstructor() {
        // When
        CourseBundle courseBundle = new CourseBundle();

        // Then
        assertThat(courseBundle.getCourseBundleId()).isEqualTo(0L);
        assertThat(courseBundle.getBundleId()).isEqualTo(0L);
        assertThat(courseBundle.getCourseId()).isEqualTo(0L);
    }

    @Test
    void testEqualsAndHashCodeAfterFieldModification() {
        // Given
        CourseBundle courseBundle1 = new CourseBundle(1L, 2L, 3L);
        CourseBundle courseBundle2 = new CourseBundle(1L, 2L, 3L);

        // Ensure they are initially equal
        assertThat(courseBundle1).isEqualTo(courseBundle2);

        // Modify one field
        courseBundle2.setCourseId(4L);

        // Then
        assertThat(courseBundle1).isNotEqualTo(courseBundle2); // No longer equal
        assertThat(courseBundle1.hashCode()).isNotEqualTo(courseBundle2.hashCode()); // Hash codes differ
    }
}