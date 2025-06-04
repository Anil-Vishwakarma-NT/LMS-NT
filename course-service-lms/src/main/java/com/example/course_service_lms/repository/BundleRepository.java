package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Bundle} entities.
 * <p>
 * Provides basic CRUD operations and custom query methods for Bundle-related data.
 * Extends {@link JpaRepository} to leverage Spring Data JPA functionalities.
 * </p>
 *
 * <p>Custom methods include checks for existence by bundle name or ID.</p>
 */
@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {

    /**
     * Checks if a bundle with the specified name exists in the database.
     *
     * @param bundleName the name of the bundle to check
     * @return {@code true} if a bundle with the given name exists, {@code false} otherwise
     */
    boolean existsByBundleName(String bundleName);

    /**
     * Checks if a bundle exists with the specified ID.
     *
     * @param id the ID of the bundle to check
     * @return {@code true} if a bundle with the given ID exists, {@code false} otherwise
     */
    boolean existsById(Long id);

    /**
     * Finds the 5 most recent bundles ordered by creation date.
     *
     * @return a list of the 5 most recently created bundles
     */
    List<Bundle> findTop5ByOrderByCreatedAtDesc();
}
