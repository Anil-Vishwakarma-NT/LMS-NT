package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.converters.BundleConverter;
import com.example.course_service_lms.dto.inDTO.BundleInDTO;
import com.example.course_service_lms.dto.outDTO.BundleOutDTO;
import com.example.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.service.BundleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
     * Converter for handling Bundle entity and DTO conversions.
     */
    @Autowired
    private BundleConverter bundleConverter;

    /**
     * Creates a new bundle based on the provided DTO.
     * <p>
     * Checks for duplicate bundle name before saving the new bundle.
     * </p>
     *
     * @param bundleInDTO the DTO containing the bundle details
     * @return the created {@link BundleOutDTO}
     * @throws ResourceAlreadyExistsException if a bundle with the same name already exists
     * @throws RuntimeException if there is a general error during bundle creation
     */
    @Override
    public BundleOutDTO createBundle(final BundleInDTO bundleInDTO) {
        try {
            log.info("Attempting to create a new bundle: {}", bundleInDTO.getBundleName());

            // Check for duplicate bundle name
            if (bundleRepository.existsByBundleName(bundleInDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleInDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, bundleInDTO.getBundleName()));
            }

            // Convert DTO to Entity using converter
            Bundle bundle = bundleConverter.toEntity(bundleInDTO);

            // Save bundle entity
            Bundle savedBundle = bundleRepository.save(bundle);
            log.info("Bundle '{}' created successfully with ID: {}", savedBundle.getBundleName(), savedBundle.getBundleId());

            // Convert entity to output DTO
            return bundleConverter.toOutDTO(savedBundle);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating bundle: {}", e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all available bundles.
     *
     * @return a list of all {@link BundleOutDTO}
     * @throws ResourceNotFoundException if no bundles are found in the system
     */
    @Override
    public List<BundleOutDTO> getAllBundles() {
        log.info("Fetching all bundles");

        List<Bundle> bundles = bundleRepository.findAll();

        if (bundles.isEmpty()) {
            log.warn("No bundles found in the system");
            throw new ResourceNotFoundException(NO_BUNDLES_FOUND);
        }

        log.info("Successfully retrieved {} bundles", bundles.size());

        // Convert entities to output DTOs
        return bundles.stream()
                .map(bundleConverter::toOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a bundle by its ID.
     *
     * @param bundleId the ID of the bundle to retrieve
     * @return the {@link BundleOutDTO} if found
     * @throws ResourceNotFoundException if bundle not found
     */
    @Override
    public BundleOutDTO getBundleById(final Long bundleId) {
        try {
            log.info("Fetching bundle with ID: {}", bundleId);

            Bundle bundle = bundleRepository.findById(bundleId)
                    .orElseThrow(() -> {
                        log.warn("Bundle with ID {} not found", bundleId);
                        return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
                    });

            log.info("Successfully retrieved bundle: {}", bundle.getBundleName());

            // Convert entity to output DTO
            return bundleConverter.toOutDTO(bundle);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching bundle with ID {}: {}", bundleId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Updates the bundle with the given ID using the provided DTO.
     * <p>
     * Validates that the new bundle name is unique (excluding the same bundle) before updating.
     * </p>
     *
     * @param bundleId  the ID of the bundle to update
     * @param updateBundleInDTO the DTO containing the updated bundle data
     * @return success message
     * @throws ResourceNotFoundException if the bundle with the specified ID does not exist
     * @throws ResourceAlreadyExistsException if the new bundle name already exists for a different bundle
     * @throws RuntimeException if there is a general error during the update
     */
    @Override
    public BundleOutDTO updateBundle(final Long bundleId, final UpdateBundleInDTO updateBundleInDTO) {
        try {
            log.info("Attempting to update bundle with ID: {}", bundleId);

            // Check if the bundle exists
            Bundle existingBundle = bundleRepository.findById(bundleId).orElseThrow(() -> {
                log.error("Bundle with ID {} not found", bundleId);
                return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
            });

            // Validate if the updated name is unique (excluding the same bundle)
            if (bundleRepository.existsByBundleName(updateBundleInDTO.getBundleName())
                    && !existingBundle.getBundleName().equalsIgnoreCase(updateBundleInDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", updateBundleInDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, updateBundleInDTO.getBundleName()));
            }

            // Update the bundle entity using converter
            Bundle updatedBundle = bundleConverter.updateEntity(existingBundle, updateBundleInDTO);
            Bundle savedBundle = bundleRepository.save(updatedBundle);

            // Convert the saved bundle to BundleOutDTO
            BundleOutDTO bundleOutDTO = bundleConverter.toOutDTO(savedBundle);

            log.info("Successfully updated bundle with ID: {}", savedBundle.getBundleId());
            return bundleOutDTO;

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating bundle with ID {}: {}", bundleId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
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
            log.error("Error deleting bundle with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
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
        try {
            log.debug("Checking existence of bundle with ID: {}", id);
            return bundleRepository.existsById(id);
        } catch (Exception e) {
            log.error("Error checking bundle existence for ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Counts the total number of bundles.
     *
     * @return the total count of bundles
     */
    @Override
    public long countBundles() {
        try {
            log.debug("Counting total bundles");
            return bundleRepository.count();
        } catch (Exception e) {
            log.error("Error counting bundles: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Retrieves the bundle name by bundle ID.
     *
     * @param bundleId the ID of the bundle
     * @return the bundle name
     * @throws ResourceNotFoundException if the bundle is not found
     * @throws RuntimeException if there is a general error
     */
    @Override
    public String getBundleNameById(Long bundleId) {
        try {
            log.info("Fetching bundle name for ID: {}", bundleId);

            Bundle bundle = bundleRepository.findById(bundleId)
                    .orElseThrow(() -> {
                        log.error("Bundle with ID {} not found", bundleId);
                        return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
                    });

            log.info("Successfully retrieved bundle name: {}", bundle.getBundleName());
            return bundle.getBundleName();

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching bundle name for ID {}: {}", bundleId, e.getMessage(), e);
            throw new RuntimeException("Something went wrong while fetching bundle name", e);
        }
    }
}