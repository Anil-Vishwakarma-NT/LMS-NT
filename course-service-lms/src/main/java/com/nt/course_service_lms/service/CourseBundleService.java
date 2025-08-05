package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.AddCourseToBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;

import java.util.List;

/**
 * Service interface for managing course-bundle relationships in the LMS.
 *
 * <p>This interface defines the contract for operations related to course-bundle
 * associations, including CRUD operations and specialized queries for bundle
 * information and summaries.</p>
 */
public interface CourseBundleService {

    /**
     * Retrieves all course-bundle associations from the system.
     *
     * @return a {@link List} of {@link CourseBundleOutDTO} representing all
     * course-bundle mappings in the system
     */
    List<CourseBundleOutDTO> getAllCourseBundles();

    /**
     * Retrieves a specific course-bundle association by its unique identifier.
     *
     * @param courseBundleId the unique identifier of the course-bundle mapping to retrieve
     * @return the {@link CourseBundleOutDTO} for the specified ID
     */
    CourseBundleOutDTO getCourseBundleById(Long courseBundleId);

    /**
     * Retrieves all course-bundle records associated with a specific bundle.
     *
     * @param bundleId the unique identifier of the bundle
     * @return a {@link List} of {@link CourseBundle} entities linked to the specified bundle
     */
    List<CourseInfoOutDTO> getAllCoursesByBundle(Long bundleId);

    /**
     * Deletes a course-bundle association by its unique identifier.
     *
     * @param courseBundleId the unique identifier of the course-bundle record to delete
     */
    void deleteCourseBundle(Long courseBundleId);

    /**
     * Updates an existing course-bundle association with new data.
     *
     * @param courseBundleId          the unique identifier of the course-bundle record to update
     * @param updateCourseBundleInDTO the data transfer object containing the updated information
     * @return a {@link String} message indicating the result of the update operation
     */
    String updateCourseBundle(Long courseBundleId, UpdateCourseBundleInDTO updateCourseBundleInDTO);

    /**
     * Creates a new course-bundle association in the system.
     *
     * @param courseBundleInDTO the data transfer object containing the details of the new course-bundle mapping
     * @return the created {@link CourseBundle} entity
     */
    CourseBundle createCourseBundle(CourseBundleInDTO courseBundleInDTO);

    /**
     * Retrieves comprehensive information about all bundles in the system.
     *
     * @return a {@link List} of {@link BundleInfoOutDTO} containing detailed bundle information
     */
    List<BundleInfoOutDTO> getBundlesInfo();

    /**
     * Retrieves summary information for recently created or modified bundles.
     *
     * @return a {@link List} of {@link BundleSummaryOutDTO} containing summary details
     */
    List<BundleSummaryOutDTO> getRecentBundleSummaries();

    /**
     * Finds all course identifiers associated with a specific bundle.
     *
     * @param bundleId the unique identifier of the bundle
     * @return a {@link List} of {@link Long} values representing course IDs linked to the bundle
     */
    List<Long> findCourseIdsByBundleId(Long bundleId);

    List<CourseInfoOutDTO> getCoursesToAdd(Long bundleId);

    StandardResponseOutDTO<MessageOutDTO> addCourseToBundle(AddCourseToBundleInDTO addCourseToBundleInDTO);

    StandardResponseOutDTO<MessageOutDTO> removeCourse(Long bundleId, Long courseId);

    StandardResponseOutDTO<List<CourseInfoOutDTO>> getBundleCourses(Long bundleId);
}
