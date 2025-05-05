package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link CourseContent} entities.
 * <p>
 * Provides methods for retrieving and validating course content entries associated with courses.
 * Extends {@link JpaRepository} for standard CRUD operations.
 * </p>
 */
@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {

    /**
     * Retrieves a {@link CourseContent} entry by its title (case-insensitive) and associated course ID.
     *
     * @param title the title of the course content
     * @param courseId the ID of the course the content belongs to
     * @return an {@link Optional} containing the matching {@link CourseContent} if found, or empty if not
     */
    Optional<CourseContent> findByTitleIgnoreCaseAndCourseId(String title, Long courseId);

    /**
     * Retrieves a list of all course content entries associated with the given course ID.
     *
     * @param courseId the ID of the course
     * @return a list of {@link CourseContent} entries belonging to the course
     */
    List<CourseContent> findByCourseId(Long courseId);
}
