package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.dto.MessageOutDTO;
import com.example.course_service_lms.dto.UpdateBundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.service.BundleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing bundle-related operations in the Course Service of the LMS.
 */
@RestController
@RequestMapping("/api/bundles")
@Slf4j
public class BundleController {

    /**
     * Service for handling bundle-related business logic.
     */
    @Autowired
    private BundleService bundleService;

    /**
     * Creates a new course bundle.
     *
     * @param bundleDTO DTO containing the details of the bundle to be created.
     * @return ResponseEntity containing the created Bundle entity.
     */
    @PostMapping
    public ResponseEntity<Bundle> createBundle(@Valid @RequestBody final BundleDTO bundleDTO) {
        log.info("Received request to create bundle: {}", bundleDTO.getBundleName());
        Bundle createdBundle = bundleService.createBundle(bundleDTO);
        return ResponseEntity.ok(createdBundle);
    }

    /**
     * Retrieves all available course bundles.
     *
     * @return ResponseEntity containing a list of all Bundle entities.
     */
    @GetMapping
    public ResponseEntity<List<Bundle>> getAllBundles() {
        log.info("Received request to fetch all bundles.");
        List<Bundle> bundles = bundleService.getAllBundles();
        return ResponseEntity.ok(bundles);
    }

    /**
     * Retrieves a specific bundle by its ID.
     *
     * @param id The ID of the bundle to retrieve.
     * @return ResponseEntity containing an Optional with the Bundle entity if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Bundle>> getBundleById(@PathVariable final Long id) {
        log.info("Received request to fetch bundle with ID: {}", id);
        Optional<Bundle> bundle = bundleService.getBundleById(id);
        return ResponseEntity.ok(bundle);
    }

    /**
     * Updates an existing bundle with the given ID.
     *
     * @param id The ID of the bundle to be updated.
     * @param updateBundleDTO DTO containing updated details of the bundle.
     * @return ResponseEntity containing the updated BundleDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBundle(@PathVariable final Long id, @Valid @RequestBody final UpdateBundleDTO updateBundleDTO) {
        log.info("Received request to update bundle with ID: {}", id);
        String response = bundleService.updateBundle(id, updateBundleDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a bundle by its ID.
     *
     * @param id The ID of the bundle to be deleted.
     * @return ResponseEntity containing a confirmation message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBundle(@PathVariable final Long id) {
        log.info("Received request to delete bundle with ID: {}", id);
        bundleService.deleteBundle(id);
        return ResponseEntity.ok("Bundle with ID " + id + " deleted successfully.");
    }

    /**
     * Checks if a bundle with the specified ID exists.
     * <p>This endpoint is primarily intended for use by the User microservice.</p>
     *
     * @param id The ID of the bundle to check.
     * @return ResponseEntity containing a boolean indicating existence.
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkIfBundleExists(@PathVariable final Long id) {
        log.info("Fetching course with ID: {}", id);
        boolean exists = bundleService.existsByBundleId(id);
        return ResponseEntity.ok(exists);
    }
    @GetMapping("/count")
    public ResponseEntity<MessageOutDTO> getCourseCount() {
        log.info("Received request to get total Bundle count.");
        long count = bundleService.countBundles();
        log.info("Total Bundle count retrieved: {}", count);
        return ResponseEntity.ok(new MessageOutDTO(""+count));
    }
}
