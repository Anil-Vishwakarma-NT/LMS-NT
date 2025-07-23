package com.nt.course_service_lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing the association between a course and a bundle
 * in the Learning Management System (LMS).
 *
 * <p>This entity maps to the {@code course_bundle} table and is used
 * to associate multiple courses to a single bundle.</p>
 */
@Entity
@Table(name = "course_bundle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseBundle {

    /**
     * Unique identifier for the course-bundle mapping.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_bundle_id")
    private long courseBundleId;

    /**
     * Foreign key referencing the {@code bundle} entity.
     * <p>This represents the ID of the bundle that the course is associated with.</p>
     */
    @Column(name = "bundle_id")
    private long bundleId;

    /**
     * Foreign key referencing the {@code course} entity.
     * <p>This represents the ID of the course that belongs to the bundle.</p>
     */
    @Column(name = "course_id")
    private long courseId;

    /**
     * Flag indicating whether the course bundle is currently active and visible to learners.
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Timestamp indicating when the course-bundle mapping was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the course-bundle mapping was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Checks equality based on all fields of the course-bundle mapping.
     *
     * @param o the object to compare
     * @return {@code true} if both mappings are equal; otherwise {@code false}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseBundle that = (CourseBundle) o;
        return courseBundleId == that.courseBundleId
                && bundleId == that.bundleId
                && courseId == that.courseId;
    }

    /**
     * Generates a hash code based on all fields of the mapping.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, courseId);
    }
}


