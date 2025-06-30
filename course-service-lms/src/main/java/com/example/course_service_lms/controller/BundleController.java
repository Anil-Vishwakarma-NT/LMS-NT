package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.inDTO.BundleInDTO;
import com.example.course_service_lms.dto.outDTO.BundleOutDTO;
import com.example.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.example.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.example.course_service_lms.service.BundleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * REST Controller for managing bundle-related operations in the Course Service of the LMS.
 * Exception handling is managed by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/bundles")
@Slf4j
@CrossOrigin("http://localhost:3000")
public class BundleController {

    /**
     * Service for handling bundle-related business logic.
     */
    @Autowired
    private BundleService bundleService;

    /**
     * Creates a new course bundle.
     *
     * @param bundleInDTO DTO containing the details of the bundle to be created.
     * @return ResponseEntity containing the StandardResponseOutDTO with created BundleOutDTO.
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> createBundle(@Valid @RequestBody final BundleInDTO bundleInDTO) {
        log.info("Received request to create bundle: {}", bundleInDTO.getBundleName());
        BundleOutDTO createdBundle = bundleService.createBundle(bundleInDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseOutDTO.success(createdBundle, "Bundle created successfully"));
    }

    /**
     * Retrieves all available course bundles.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with a list of all BundleOutDTO.
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> getAllBundles() {
        log.info("Received request to fetch all bundles.");
        List<BundleOutDTO> bundles = bundleService.getAllBundles();
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundles, "All bundles retrieved successfully"));
    }

    /**
     * Retrieves a specific bundle by its ID.
     *
     * @param id The ID of the bundle to retrieve.
     * @return ResponseEntity containing StandardResponseOutDTO with the BundleOutDTO if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> getBundleById(@PathVariable final Long id) {
        log.info("Received request to fetch bundle with ID: {}", id);
        BundleOutDTO bundle = bundleService.getBundleById(id);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundle, "Bundle retrieved successfully"));
    }

    /**
     * Updates an existing bundle with the given ID.
     *
     * @param id The ID of the bundle to be updated.
     * @param updateBundleInDTO DTO containing updated details of the bundle.
     * @return ResponseEntity containing StandardResponseOutDTO with update confirmation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<BundleOutDTO>> updateBundle(@PathVariable final Long id,
                                                                             @Valid @RequestBody final UpdateBundleInDTO updateBundleInDTO) {
        log.info("Received request to update bundle with ID: {}", id);
        BundleOutDTO bundleOutDTO = bundleService.updateBundle(id, updateBundleInDTO);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleOutDTO, "Bundle updated successfully"));
    }

    /**
     * Deletes a bundle by its ID.
     *
     * @param id The ID of the bundle to be deleted.
     * @return ResponseEntity containing StandardResponseOutDTO with deletion confirmation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteBundle(@PathVariable final Long id) {
        log.info("Received request to delete bundle with ID: {}", id);
        bundleService.deleteBundle(id);
        String message = "Bundle with ID " + id + " deleted successfully.";
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "Bundle deleted successfully"));
    }

    /**
     * Checks if a bundle with the specified ID exists.
     * <p>This endpoint is primarily intended for use by the User microservice.</p>
     *
     * @param id The ID of the bundle to check.
     * @return ResponseEntity containing StandardResponseOutDTO with boolean indicating existence.
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<StandardResponseOutDTO<Boolean>> checkIfBundleExists(@PathVariable final Long id) {
        log.info("Checking if bundle exists with ID: {}", id);
        boolean exists = bundleService.existsByBundleId(id);
        String message = exists ? "Bundle exists" : "Bundle does not exist";
        return ResponseEntity.ok(StandardResponseOutDTO.success(exists, message));
    }

    /**
     * Gets the total count of bundles.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with bundle count.
     */
    @GetMapping("/count")
    public ResponseEntity<StandardResponseOutDTO<Long>> getBundleCount() {
        log.info("Received request to get total Bundle count.");
        long count = bundleService.countBundles();
        log.info("Total Bundle count retrieved: {}", count);
        return ResponseEntity.ok(StandardResponseOutDTO.success(count, "Bundle count retrieved successfully"));
    }

    /**
     * Gets the name of a bundle by its ID.
     *
     * @param id The ID of the bundle.
     * @return ResponseEntity containing StandardResponseOutDTO with bundle name.
     */
    @GetMapping("/{id}/name")
    public ResponseEntity<StandardResponseOutDTO<String>> getBundleNameById(@PathVariable("id") Long id) {
        log.info("Received request to get bundle name for ID: {}", id);
        String bundleName = bundleService.getBundleNameById(id);
        log.info("Bundle name retrieved: {}", bundleName);
        return ResponseEntity.ok(StandardResponseOutDTO.success(bundleName, "Bundle name retrieved successfully"));
    }

    @PostMapping("/existing-ids")
    public ResponseEntity<List<Long>> getExistingBundleIds(@RequestBody List<Long> bundleIds) {
        List<Long> existingIds = bundleService.findExistingIds(bundleIds);
        return ResponseEntity.ok(existingIds);
    }
}