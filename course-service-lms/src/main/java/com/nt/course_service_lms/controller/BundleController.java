package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.BundleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing bundle-related operations in the Course Service of the LMS.
 * This controller provides endpoints for creating, retrieving, updating, and deleting course bundles.
 * All operations return standardized response DTOs and proper HTTP status codes.
 * Exception handling is managed by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/service-api/bundles")
@Slf4j
public class BundleController {

    /**
     * Service layer component responsible for handling bundle-related business logic operations.
     * This service encapsulates all business rules and data access logic for bundle management.
     */
    @Autowired
    private BundleService bundleService;

    /**
     * Creates a new course bundle in the system.
     * Validates the input data and delegates the creation logic to the service layer.
     *
     * @param bundleInDTO The data transfer object containing all necessary information
     *                    to create a new bundle including name, description, and other properties.
     *                    Must not be null and must pass validation constraints.
     * @return ResponseEntity containing a StandardResponseOutDTO with the created BundleOutDTO
     * and HTTP status 201 (CREATED) on successful creation
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> createBundle(@Valid @RequestBody final BundleInDTO bundleInDTO) {
        log.info("Received request to create bundle: {}", bundleInDTO.getBundleName());
        StandardResponseOutDTO<BundleOutDTO> createdBundle = bundleService.createBundle(bundleInDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdBundle);
    }

    /**
     * Retrieves all available course bundles from the system.
     * Returns a complete list of all bundles with their details.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with a List of BundleOutDTO
     * representing all available bundles and HTTP status 200 (OK)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> getAllBundles() {
        log.info("Received request to fetch all bundles.");
        List<BundleOutDTO> bundles = bundleService.getAllBundles();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundles, "All bundles retrieved successfully"));
    }

    /**
     * Retrieves a specific bundle by its unique identifier.
     *
     * @param id The unique identifier of the bundle to retrieve. Must be a valid Long value.
     * @return ResponseEntity containing a StandardResponseOutDTO with the BundleOutDTO
     * if found and HTTP status 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> getBundleById(@PathVariable final Long id) {
        log.info("Received request to fetch bundle with ID: {}", id);
        BundleOutDTO bundle = bundleService.getBundleById(id);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundle, "Bundle retrieved successfully"));
    }


    /**
     * Retrieves a specific bundle by its ID.
     *
     * @param bundleIds The ID of the bundle to retrieve.
     * @return ResponseEntity containing StandardResponseOutDTO with the BundleOutDTO if found.
     */
    @PostMapping("/meta-bundles")
    public ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> getBundlesByIds(@RequestBody final List<Long> bundleIds) {
        log.info("Received request to fetch bundle with IDs");
        StandardResponseOutDTO<List<BundleOutDTO>> bundle = bundleService.getBundlesByIds(bundleIds);
        return  new ResponseEntity<>(bundle , HttpStatus.OK);
    }

    /**
     * Updates an existing bundle with new information.
     * Only updates the fields provided in the UpdateBundleInDTO.
     *
     * @param id                The unique identifier of the bundle to be updated. Must be a valid Long value.
     * @param updateBundleInDTO The data transfer object containing the updated bundle information.
     *                          Must not be null and must pass validation constraints.
     * @return ResponseEntity containing a StandardResponseOutDTO with the updated BundleOutDTO
     * and HTTP status 200 (OK) on successful update
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> updateBundle(@PathVariable final Long id,
                                                                             @Valid @RequestBody final
                                                                             UpdateBundleInDTO updateBundleInDTO) {
        log.info("Received request to update bundle with ID: {}", id);
        BundleOutDTO bundleOutDTO = bundleService.updateBundle(id, updateBundleInDTO);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleOutDTO, "Bundle updated successfully"));
    }

    /**
     * Deletes a bundle from the system by its unique identifier.
     * This operation is irreversible and will remove all associated data.
     *
     * @param id The unique identifier of the bundle to be deleted. Must be a valid Long value.
     * @return ResponseEntity containing a StandardResponseOutDTO with null data
     * and HTTP status 200 (OK) on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteBundle(@PathVariable final Long id) {
        log.info("Received request to delete bundle with ID: {}", id);
        bundleService.deleteBundle(id);
        String message ="Bundle with ID " + id + " deleted successfully.";
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "Bundle deleted successfully"));
    }

    /**
     * Checks if a bundle with the specified ID exists in the system.
     * This endpoint is primarily intended for use by other microservices
     * to validate bundle existence before performing operations.
     *
     * @param id The unique identifier of the bundle to check for existence. Must be a valid Long value.
     * @return ResponseEntity containing a StandardResponseOutDTO with a Boolean value
     * indicating whether the bundle exists and HTTP status 200 (OK)
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<StandardResponseOutDTO<Boolean>> checkIfBundleExists(@PathVariable final Long id) {
        log.info("Checking if bundle exists with ID: {}", id);
        boolean exists = bundleService.existsByBundleId(id);
        String message = exists ? "Bundle exists" : "Bundle does not exist";
        return ResponseEntity.ok(StandardResponseOutDTO.success(exists, message));
    }

    /**
     * Retrieves the total count of bundles in the system.
     * This endpoint provides statistical information about the number of bundles.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with a Long value
     * representing the total bundle count and HTTP status 200 (OK)
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Long>> getBundleCount() {
        log.info("Received request to get total Bundle count.");
        long count = bundleService.countBundles();
        log.info("Total Bundle count retrieved: {}", count);
        return ResponseEntity.ok(StandardResponseOutDTO.success(count, "Bundle count retrieved successfully"));
    }

    /**
     * Retrieves the name of a bundle by its unique identifier.
     * This endpoint provides a lightweight way to get only the bundle name
     * without fetching the complete bundle details.
     *
     * @param id The unique identifier of the bundle whose name is to be retrieved.
     *           Must be a valid Long value.
     * @return ResponseEntity containing a StandardResponseOutDTO with the bundle name
     * as a String and HTTP status 200 (OK)
     */
    @GetMapping("/{id}/name")
    public ResponseEntity<StandardResponseOutDTO<String>> getBundleNameById(@PathVariable("id") final Long id) {
        log.info("Received request to get bundle name for ID: {}", id);
        String bundleName = bundleService.getBundleNameById(id);
        log.info("Bundle name retrieved: {}", bundleName);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleName, "Bundle name retrieved successfully"));
    }

    /**
     * Filters a list of bundle IDs to return only those that exist in the system.
     * This endpoint is useful for bulk operations where you need to validate
     * multiple bundle IDs at once.
     *
     * @param bundleIds A list of bundle IDs to check for existence. Must not be null.
     * @return ResponseEntity containing a List of Long values representing the bundle IDs
     * that exist in the system and HTTP status 200 (OK)
     */
    @PostMapping("/existing-ids")
    public ResponseEntity<List<Long>> getExistingBundleIds(@RequestBody final List<Long> bundleIds) {
        List<Long> existingIds = bundleService.findExistingIds(bundleIds);
        return ResponseEntity.ok(existingIds);
    }
}