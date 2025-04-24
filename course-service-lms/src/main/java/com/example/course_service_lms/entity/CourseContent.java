package com.example.course_service_lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing the course content in the Learning Management System (LMS).
 *
 * <p>This entity maps to the {@code course_content} table and stores
 * individual content sections for a course such as titles, descriptions,
 * video links, and optional resources.</p>
 */
@Entity
@Table(name = "course_content")
@Data
public class CourseContent {

    /**
     * Unique identifier for each course content section.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_content_id")
    private long courseContentId;

    /**
     * The ID of the course to which this content belongs.
     */
    @Column(name = "course_id")
    private long courseId;

    /**
     * The title of the course content section.
     */
    @Column(name = "title")
    private String title;

    /**
     * A detailed description of the content section.
     */
    @Column(name = "description")
    private String description;

    /**
     * An optional URL link to an external resource (e.g., reading material).
     */
    @Column(name = "resource_link")
    private String resourceLink;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default no-args constructor required by JPA.
     */
    public CourseContent() {
    }

    /**
     * All-args constructor for manually creating a course content instance.
     *
     * @param courseContentId the ID of the course content
     * @param courseId        the ID of the course
     * @param title           the title of the content
     * @param description     the content description
     * @param resourceLink    the optional resource link
     */
    public CourseContent(final long courseContentId, final long courseId, final String title,
                         final String description, final String resourceLink) {
        this.courseContentId = courseContentId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.resourceLink = resourceLink;
    }

    /**
     * Compares this object with another for equality based on all fields.
     *
     * @param o the object to compare
     * @return {@code true} if both instances are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContent that = (CourseContent) o;
        return courseContentId == that.courseContentId
                && courseId == that.courseId
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(resourceLink, that.resourceLink);
    }

    /**
     * Generates a hash code based on all fields of the entity.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseContentId, courseId, title, description, resourceLink);
    }
}
