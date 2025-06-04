package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.*;
import com.example.course_service_lms.entity.CourseBundle;

import java.util.List;

/**
 * Service interface for managing course-bundle relationships.
 * <p>
 * Provides methods for creating, updating, retrieving, and deleting
 * {@link CourseBundle} associations.
 * </p>
 */
public interface CourseBundleService {

    /**
     * Retrieves all course-bundle associations.
     *
     * @return a list of {@link CourseBundleDTO} representing all course-bundle mappings
     */
    List<CourseBundleDTO> getAllCourseBundles();

    /**
     * Retrieves a specific course-bundle association by its ID.
     *
     * @param courseBundleId the ID of the course-bundle mapping to retrieve
     * @return the {@link CourseBundleDTO} for the given ID
     */
    CourseBundleDTO getCourseBundleById(Long courseBundleId);

    /**
     * Retrieves all course-bundle records associated with a specific bundle.
     *
     * @param bundleId the ID of the bundle
     * @return a list of {@link CourseBundle} entities linked to the specified bundle
     */
    List<CourseBundle> getAllCoursesByBundle(Long bundleId);

    /**
     * Deletes a course-bundle association by its ID.
     *
     * @param courseBundleId the ID of the course-bundle record to delete
     */
    void deleteCourseBundle(Long courseBundleId);

    /**
     * Updates an existing course-bundle association.
     *
     * @param courseBundleId the ID of the course-bundle record to update
     * @param updateCourseBundleDTO the new data for the course-bundle mapping
     * @return the updated {@link CourseBundlePostDTO}
     */
    String updateCourseBundle(Long courseBundleId, UpdateCourseBundleDTO updateCourseBundleDTO);

    /**
     * Creates a new course-bundle association.
     *
     * @param courseBundlePostDTO the details of the new course-bundle mapping
     * @return the created {@link CourseBundlePostDTO}
     */
    CourseBundle createCourseBundle(CourseBundlePostDTO courseBundlePostDTO);

    List<BundleInfoDTO> getBundlesInfo();
    List<BundleSummaryDTO> getRecentBundleSummaries();
}
