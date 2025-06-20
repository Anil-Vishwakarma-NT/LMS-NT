package com.example.course_service_lms.service;

import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.example.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.example.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.example.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import org.springframework.data.repository.query.Param;

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
     * @return a list of {@link CourseBundleOutDTO} representing all course-bundle mappings
     */
    List<CourseBundleOutDTO> getAllCourseBundles();

    /**
     * Retrieves a specific course-bundle association by its ID.
     *
     * @param courseBundleId the ID of the course-bundle mapping to retrieve
     * @return the {@link CourseBundleOutDTO} for the given ID
     */
    CourseBundleOutDTO getCourseBundleById(Long courseBundleId);

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
     * @param updateCourseBundleInDTO the new data for the course-bundle mapping
     * @return the updated {@link CourseBundleInDTO}
     */
    String updateCourseBundle(Long courseBundleId, UpdateCourseBundleInDTO updateCourseBundleInDTO);

    /**
     * Creates a new course-bundle association.
     *
     * @param courseBundleInDTO the details of the new course-bundle mapping
     * @return the created {@link CourseBundleInDTO}
     */
    CourseBundle createCourseBundle(CourseBundleInDTO courseBundleInDTO);

    List<BundleInfoOutDTO> getBundlesInfo();
    List<BundleSummaryOutDTO> getRecentBundleSummaries();

    List<Long> findCourseIdsByBundleId(Long bundleId);
}
