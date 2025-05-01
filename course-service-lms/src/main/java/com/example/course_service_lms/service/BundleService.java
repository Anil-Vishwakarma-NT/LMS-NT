package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.dto.BundleSummaryDTO;
import com.example.course_service_lms.dto.UpdateBundleDTO;
import com.example.course_service_lms.entity.Bundle;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling business logic related to {@link Bundle} entities.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting course bundles,
 * as well as checking for their existence by ID.
 * </p>
 */
public interface BundleService {

    /**
     * Creates a new bundle based on the provided {@link BundleDTO}.
     *
     * @param bundleDTO the data transfer object containing bundle details
     * @return the newly created {@link Bundle} entity
     */
    Bundle createBundle(BundleDTO bundleDTO);

    /**
     * Retrieves all existing bundles.
     *
     * @return a list of all {@link Bundle} entities
     */
    List<Bundle> getAllBundles();

    /**
     * Retrieves a bundle by its ID.
     *
     * @param id the ID of the bundle to retrieve
     * @return an {@link Optional} containing the bundle if found, or empty otherwise
     */
    Optional<Bundle> getBundleById(Long id);

    /**
     * Updates the details of an existing bundle.
     *
     * @param bundleId the ID of the bundle to update
     * @param updateBundleDTO the updated bundle data
     * @return the updated {@link BundleDTO}
     */
    String updateBundle(Long bundleId, UpdateBundleDTO updateBundleDTO);

    /**
     * Deletes the bundle with the given ID.
     *
     * @param id the ID of the bundle to delete
     */
    void deleteBundle(Long id);

    /**
     * Checks whether a bundle exists by its ID.
     *
     * @param id the ID to check
     * @return {@code true} if a bundle with the given ID exists, {@code false} otherwise
     */
    boolean existsByBundleId(Long id);

    long countBundles();

    String getBundleNameById(Long bundleId);

    /**
     * Retrieves summaries of the 5 most recent bundles with course counts
     *
     * @return a list containing summaries of the most recently created bundles with course counts
     */

}
