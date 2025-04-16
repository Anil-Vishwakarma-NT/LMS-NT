package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.service.BundleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_ALREADY_EXISTS;
import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_NOT_FOUND_BY_ID;
import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.BundleConstants.NO_BUNDLES_FOUND;

/**
 * Implementation of the {@link BundleService} interface that manages bundle-related operations.
 * Provides functionality for creating, updating, deleting, and retrieving bundles.
 * Handles validation, error handling, and logging for these operations.
 *
 * @see BundleService
 */
@Service
@Slf4j
public class BundleServiceImpl implements BundleService {

    /**
     * The repository responsible for performing CRUD operations on the {@link Bundle} entity.
     */
    @Autowired
    private BundleRepository bundleRepository;

    /**
     * Creates a new bundle based on the provided DTO.
     * <p>
     * Checks for duplicate bundle name before saving the new bundle.
     * </p>
     *
     * @param bundleDTO the DTO containing the bundle details
     * @return the created {@link Bundle} entity
     * @throws ResourceAlreadyExistsException if a bundle with the same name already exists
     * @throws RuntimeException if there is a general error during bundle creation
     */
    @Override
    public Bundle createBundle(final BundleDTO bundleDTO) {
        try {
            log.info("Attempting to create a new bundle: {}", bundleDTO.getBundleName());

            // Check for duplicate bundle name
            if (bundleRepository.existsByBundleName(bundleDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, bundleDTO.getBundleName()));
            }
            // Convert DTO to Entity
            Bundle bundle = new Bundle();
            bundle.setBundleName(bundleDTO.getBundleName());

            // Save bundle entity
            Bundle savedBundle = bundleRepository.save(bundle);
            log.info("Bundle '{}' created successfully with ID: {}", savedBundle.getBundleName(), savedBundle.getBundleId());

            // Convert Entity to DTO before returning
            return savedBundle;
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all available bundles.
     *
     * @return a list of all {@link Bundle} entities
     * @throws ResourceNotFoundException if no bundles are found in the system
     */
    @Override
    public List<Bundle> getAllBundles() {
        log.info("Fetching all bundles");

        List<Bundle> bundles = bundleRepository.findAll();

        if (bundles.isEmpty()) {
            log.warn("No bundles found in the system");
            throw new ResourceNotFoundException(NO_BUNDLES_FOUND);
        }

        log.info("Successfully retrieved {} bundles", bundles.size());
        return bundles;
    }

    /**
     * Retrieves a bundle by its ID.
     *
     * @param bundleId the ID of the bundle to retrieve
     * @return an {@link Optional} containing the bundle if found, or empty if not
     * @throws ResourceNotFoundException if the bundle with the specified ID does not exist
     */
    @Override
    public Optional<Bundle> getBundleById(final Long bundleId) {
        try {
            log.info("Fetching bundle with ID: {}", bundleId);

            Optional<Bundle> optionalBundle = bundleRepository.findById(bundleId);

            if (!optionalBundle.isPresent()) {
                log.error("Bundle with ID {} not found", bundleId);
                throw new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
            }

            log.info("Successfully retrieved bundle: {}", optionalBundle.get().getBundleName());
            return optionalBundle;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Updates the bundle with the given ID using the provided DTO.
     * <p>
     * Validates that the new bundle name is unique (excluding the same bundle) before updating.
     * </p>
     *
     * @param bundleId  the ID of the bundle to update
     * @param bundleDTO the DTO containing the updated bundle data
     * @return the updated {@link BundleDTO}
     * @throws ResourceNotFoundException if the bundle with the specified ID does not exist
     * @throws ResourceAlreadyExistsException if the new bundle name already exists for a different bundle
     * @throws RuntimeException if there is a general error during the update
     */
    @Override
    public BundleDTO updateBundle(final Long bundleId, final BundleDTO bundleDTO) {
        try {
            log.info("Attempting to update bundle with ID: {}", bundleId);
            // Check if the bundle exists
            Bundle existingBundle = bundleRepository.findById(bundleId).orElseThrow(() -> {
                log.error("Bundle with ID {} not found", bundleId);
                return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
            });

            // Validate if the updated name is unique (excluding the same bundle)
            if (bundleRepository.existsByBundleName(bundleDTO.getBundleName())
                    && !existingBundle.getBundleName().equalsIgnoreCase(bundleDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, bundleDTO.getBundleName()));
            }

            // Update the bundle entity
            existingBundle.setBundleName(bundleDTO.getBundleName());
            Bundle updatedBundle = bundleRepository.save(existingBundle);
            log.info("Successfully updated bundle with ID: {}", updatedBundle.getBundleId());

            // Convert to DTO before returning
            return new BundleDTO(updatedBundle.getBundleName());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Deletes the bundle with the specified ID.
     *
     * @param id the ID of the bundle to delete
     * @throws ResourceNotFoundException if the bundle with the specified ID does not exist
     * @throws RuntimeException if there is a general error during the deletion
     */
    @Override
    public void deleteBundle(final Long id) {
        try {
            log.info("Attempting to delete bundle with ID: {}", id);

            // Check if the bundle exists
            Bundle existingBundle = bundleRepository.findById(id).orElseThrow(() -> {
                log.error("Bundle with ID {} not found", id);
                return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, id));
            });
            // Delete the bundle
            bundleRepository.delete(existingBundle);
            log.info("Successfully deleted bundle with ID: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Checks whether a bundle exists by its ID.
     *
     * @param id the ID of the bundle
     * @return {@code true} if the bundle exists, {@code false} otherwise
     */
    @Override
    public boolean existsByBundleId(final Long id) {
        return bundleRepository.existsById(id);
    }
}

