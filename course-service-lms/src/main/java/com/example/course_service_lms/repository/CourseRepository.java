package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Course} entities.
 * <p>
 * Provides methods to interact with the course data, such as checking for existence
 * and retrieving courses by specific fields.
 * Extends {@link JpaRepository} to inherit standard CRUD operations.
 * </p>
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its title (case-insensitive) and the owner's ID.
     *
     * @param title the title of the course
     * @param ownerId the ID of the course owner
     * @return an {@link Optional} containing the matching {@link Course}, or empty if none found
     */
    Optional<Course> findByTitleIgnoreCaseAndOwnerId(String title, Long ownerId);

    /**
     * Checks whether a course exists with the given ID.
     *
     * @param id the course ID to check
     * @return {@code true} if a course with the ID exists, {@code false} otherwise
     */
    boolean existsById(Long id);

    List<Course> findTop5ByOrderByCreatedAtDesc();
}
