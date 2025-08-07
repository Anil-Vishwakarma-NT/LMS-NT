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
 * <p>
 * This interface defines operations for CRUD and retrieval functionalities related
 * to mapping courses with bundles, as well as handling bundle information and summaries.
 */
public interface CourseBundleService {

    /**
     * Retrieves all course-bundle mappings in the system.
     *
     * @return list of {@link CourseBundleOutDTO} representing all course-bundle mappings
     */
    List<CourseBundleOutDTO> getAllCourseBundles();

    /**
     * Retrieves details of a specific course-bundle mapping by its ID.
     *
     * @param courseBundleId unique identifier of the course-bundle record
     * @return {@link CourseBundleOutDTO} containing mapping details
     */
    CourseBundleOutDTO getCourseBundleById(Long courseBundleId);

    /**
     * Fetches all courses linked to a specific bundle.
     *
     * @param bundleId ID of the bundle
     * @return list of {@link CourseInfoOutDTO} representing courses in the bundle
     */
    List<CourseInfoOutDTO> getAllCoursesByBundle(Long bundleId);

    /**
     * Deletes a specific course-bundle mapping.
     *
     * @param courseBundleId unique identifier of the course-bundle record to delete
     */
    void deleteCourseBundle(Long courseBundleId);

    /**
     * Updates an existing course-bundle mapping with new information.
     *
     * @param courseBundleId          ID of the course-bundle to update
     * @param updateCourseBundleInDTO DTO containing updated information
     * @return a message string indicating result of update operation
     */
    String updateCourseBundle(Long courseBundleId, UpdateCourseBundleInDTO updateCourseBundleInDTO);

    /**
     * Creates a new course-bundle mapping.
     *
     * @param courseBundleInDTO DTO with details for the new mapping
     * @return created {@link CourseBundle} entity
     */
    CourseBundle createCourseBundle(CourseBundleInDTO courseBundleInDTO);

    /**
     * Retrieves detailed information about all bundles.
     *
     * @return list of {@link BundleInfoOutDTO} containing detailed bundle info
     */
    List<BundleInfoOutDTO> getBundlesInfo();

    /**
     * Fetches summaries of recently created or updated bundles.
     *
     * @return list of {@link BundleSummaryOutDTO} for recent bundles
     */
    List<BundleSummaryOutDTO> getRecentBundleSummaries();

    /**
     * Retrieves all course IDs linked to a specific bundle.
     *
     * @param bundleId ID of the bundle
     * @return list of course IDs associated with the bundle
     */
    List<Long> findCourseIdsByBundleId(Long bundleId);

    /**
     * Retrieves a list of courses that are eligible to be added to a bundle.
     *
     * @param bundleId ID of the bundle
     * @return list of {@link CourseInfoOutDTO} for addable courses
     */
    List<CourseInfoOutDTO> getCoursesToAdd(Long bundleId);

    /**
     * Adds a course to a specific bundle.
     *
     * @param addCourseToBundleInDTO DTO containing course and bundle info
     * @return standard response containing result message
     */
    StandardResponseOutDTO<MessageOutDTO> addCourseToBundle(AddCourseToBundleInDTO addCourseToBundleInDTO);

    /**
     * Removes a course from a specific bundle.
     *
     * @param bundleId ID of the bundle
     * @param courseId ID of the course to remove
     * @return standard response containing result message
     */
    StandardResponseOutDTO<MessageOutDTO> removeCourse(Long bundleId, Long courseId);

    /**
     * Retrieves the list of courses currently present in a bundle.
     *
     * @param bundleId ID of the bundle
     * @return standard response containing list of {@link CourseInfoOutDTO}
     */
    StandardResponseOutDTO<List<CourseInfoOutDTO>> getBundleCourses(Long bundleId);
}
