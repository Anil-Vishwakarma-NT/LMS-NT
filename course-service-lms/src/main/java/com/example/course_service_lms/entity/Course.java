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
     * Optional image URL or path for the course thumbnail or banner.
     */
    @Column(name = "image")
    private String image;

    /**
     * Level of the course (e.g., BEGINNER, INTERMEDIATE, ADVANCED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private CourseLevel level;

    /**
     * All-args constructor used for manually creating a Course instance.
     *
     * @param courseId     the course ID
     * @param ownerId      the owner/instructor ID
     * @param title        the course title
     * @param description  the course description
     * @param image        the image path or URL
     * @param level        the course difficulty level
     */
    public Course(final long courseId, final long ownerId, final String title,
                  final String description, final String image, final CourseLevel level) {
        this.courseId = courseId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.image = image;
        this.level = level;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Course() {
    }

    /**
     * Checks equality based on all fields of the course.
     *
     * @param o the object to compare
     * @return {@code true} if the courses are equal; otherwise {@code false}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return courseId == course.courseId
                && ownerId == course.ownerId
                && Objects.equals(title, course.title)
                && Objects.equals(description, course.description)
                && Objects.equals(image, course.image)
                && level == course.level;
    }

    /**
     * Generates a hash code based on all fields of the course.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, ownerId, title, description, image, level);
    }
}

