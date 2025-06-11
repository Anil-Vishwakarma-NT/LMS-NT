package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.BundleInDTO;
import com.example.course_service_lms.dto.outDTO.BundleOutDTO;
import com.example.course_service_lms.dto.inDTO.UpdateBundleInDTO;
import com.example.course_service_lms.entity.Bundle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Converter class for Bundle entity and DTOs.
 * Handles conversion between different representations of Bundle data.
 */
@Component
public class BundleConverter {

    /**
     * Converts BundleDTO to Bundle entity for creation.
     *
     * @param bundleInDTO the DTO to convert
     * @return new Bundle entity
     */
    public Bundle toEntity(BundleInDTO bundleInDTO) {
        if (bundleInDTO == null) {
            return null;
        }

        Bundle bundle = new Bundle();
        bundle.setBundleName(bundleInDTO.getBundleName());
        bundle.setActive(bundleInDTO.isActive());
        bundle.setCreatedAt(LocalDateTime.now());
        bundle.setUpdatedAt(LocalDateTime.now());

        return bundle;
    }

    /**
     * Updates existing Bundle entity with UpdateBundleDTO data.
     *
     * @param existingBundle the bundle to update
     * @param updateBundleInDTO the update data
     * @return updated Bundle entity
     */
    public Bundle updateEntity(Bundle existingBundle, UpdateBundleInDTO updateBundleInDTO) {
        if (existingBundle == null || updateBundleInDTO == null) {
            return existingBundle;
        }

        existingBundle.setBundleName(updateBundleInDTO.getBundleName());
        existingBundle.setActive(updateBundleInDTO.isActive());
        existingBundle.setUpdatedAt(LocalDateTime.now());

        return existingBundle;
    }

    /**
     * Converts Bundle entity to BundleDTO.
     *
     * @param bundle the entity to convert
     * @return BundleDTO
     */
    public BundleInDTO toDTO(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        return new BundleInDTO(bundle.getBundleName(), bundle.isActive());
    }

    /**
     * Converts Bundle entity to BundleOutDTO for API responses.
     *
     * @param bundle the Bundle entity to convert
     * @return the converted BundleOutDTO
     */
    public BundleOutDTO toOutDTO(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        BundleOutDTO bundleOutDTO = new BundleOutDTO();
        bundleOutDTO.setBundleId(bundle.getBundleId());
        bundleOutDTO.setBundleName(bundle.getBundleName());
        bundleOutDTO.setActive(bundle.isActive());
        bundleOutDTO.setCreatedAt(bundle.getCreatedAt());
        bundleOutDTO.setUpdatedAt(bundle.getUpdatedAt());

        return bundleOutDTO;
    }
}