package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.service.BundleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bundles")
//@RequiredArgsConstructor
@Slf4j
public class BundleController {

    @Autowired
    private BundleService bundleService;

    /**
     * Create a new bundle.
     */
    @PostMapping("/create")
    public ResponseEntity<Bundle> createBundle(@Valid @RequestBody BundleDTO bundleDTO) {
        log.info("Received request to create bundle: {}", bundleDTO.getBundleName());
        Bundle createdBundle = bundleService.createBundle(bundleDTO);
        return ResponseEntity.ok(createdBundle);
    }

    /**
     * Get all bundles.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Bundle>> getAllBundles() {
        log.info("Received request to fetch all bundles.");
        List<Bundle> bundles = bundleService.getAllBundles();
        return ResponseEntity.ok(bundles);
    }

    /**
     * Get a bundle by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Bundle>> getBundleById(@PathVariable Long id) {
        log.info("Received request to fetch bundle with ID: {}", id);
        Optional<Bundle> bundle = bundleService.getBundleById(id);
        return ResponseEntity.ok(bundle);
    }

    /**
     * Update a bundle.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<BundleDTO> updateBundle(@PathVariable Long id, @Valid @RequestBody BundleDTO bundleDTO) {
        log.info("Received request to update bundle with ID: {}", id);
        BundleDTO updatedBundle = bundleService.updateBundle(id, bundleDTO);
        return ResponseEntity.ok(updatedBundle);
    }

    /**
     * Delete a bundle.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBundle(@PathVariable Long id) {
        log.info("Received request to delete bundle with ID: {}", id);
        bundleService.deleteBundle(id);
        return ResponseEntity.ok("Bundle with ID " + id + " deleted successfully.");
    }
}
