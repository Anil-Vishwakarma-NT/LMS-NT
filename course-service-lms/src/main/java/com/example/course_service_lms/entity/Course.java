package com.example.course_service_lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a course in the Learning Management System (LMS).
 *
 * <p>This entity holds the details of a course including metadata such as title,
 * description, level, and the owner who created the course.</p>
 *
 * <p>This class maps to the {@code course} table in the database.</p>
 */
@Entity
@Table(name = "course")
@Data
public class Course {

    /**
     * Unique identifier for the course.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private long courseId;

    /**
     * Identifier for the owner (e.g., instructor or admin) who created the course.
     */
    @Column(name = "ownerId")
    private long ownerId;

    /**
     * Title of the course.
     * <p>This is typically a short name describing the course.</p>
     */
    @Column(name = "title")
    private String title;

    /**
     * Detailed description of the course content and purpose.
     */
    @Column(name = "description")
    private String description;

    /**
     * Level of the course (e.g., BEGINNER, INTERMEDIATE, ADVANCED).
     */
    @Column(name = "level")
    private String level;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * All-args constructor used for manually creating a Course instance.
     *
     * @param courseId     the course ID
     * @param ownerId      the owner/instructor ID
     * @param title        the course title
     * @param description  the course description
     * @param level        the course difficulty level
     */
    public Course(long courseId, long ownerId, String title, String description, String level, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.courseId = courseId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Course() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId && ownerId == course.ownerId && isActive == course.isActive && Objects.equals(title, course.title) && Objects.equals(description, course.description) && Objects.equals(level, course.level) && Objects.equals(createdAt, course.createdAt) && Objects.equals(updatedAt, course.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, ownerId, title, description, level, isActive, createdAt, updatedAt);
    }
}

