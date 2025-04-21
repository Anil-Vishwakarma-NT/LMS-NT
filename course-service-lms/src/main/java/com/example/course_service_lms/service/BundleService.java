package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;

import java.util.List;
import java.util.Optional;

public interface BundleService {

    Bundle createBundle(BundleDTO bundleDTO);

    List<Bundle> getAllBundles();

    Optional<Bundle> getBundleById(Long id);

    BundleDTO updateBundle(Long bundleId, BundleDTO bundleDTO);

    void deleteBundle(Long id);

    boolean existsByBundleId(Long id);

    long countBundles();
}
