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
 * Represents a course entity in the Learning Management System (LMS).
 *
 * <p>Each instance corresponds to a single row in the {@code course} table and
 * captures the essential metadata of a course, such as its title, description,
 * difficulty level, owner, and audit timestamps.</p>
 */
@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    /**
     * Primary key for the course (auto‑generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private long courseId;

    /**
     * Identifier of the user (e.g., instructor or admin) who created this course.
     */
    @Column(name = "ownerId")
    private long ownerId;

    /**
     * Human‑readable title of the course.
     */
    @Column(name = "title")
    private String title;

    /**
     * Rich description outlining the course objectives, scope, and content.
     */
    @Column(name = "description")
    private String description;

    /**
     * Difficulty level of the course (e.g., {@code BEGINNER}, {@code INTERMEDIATE}, {@code ADVANCED}).
     */
    @Column(name = "level")
    private String level;

    /**
     * Flag indicating whether the course is currently active and visible to learners.
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Timestamp when the course record was first created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp for the last update made to the course record.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Determines equality based on all persistent fields.
     *
     * @param o object to compare
     * @return {@code true} if both objects represent the same course; {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return courseId == course.courseId
                && ownerId == course.ownerId
                && isActive == course.isActive
                && Objects.equals(title, course.title)
                && Objects.equals(description, course.description)
                && Objects.equals(level, course.level)
                && Objects.equals(createdAt, course.createdAt)
                && Objects.equals(updatedAt, course.updatedAt);
    }

    /**
     * Generates a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code based on all persistent fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, ownerId, title, description, level, isActive, createdAt, updatedAt);
    }
}
