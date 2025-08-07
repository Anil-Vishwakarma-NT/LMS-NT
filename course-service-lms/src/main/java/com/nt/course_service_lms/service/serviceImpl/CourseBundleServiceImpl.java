package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.converters.CourseConvertors;
import com.nt.course_service_lms.dto.inDTO.AddCourseToBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.repository.CourseBundleRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nt.course_service_lms.constants.CourseBundleConstants.BUNDLE_NOT_FOUND;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_BUNDLE_ALREADY_EXISTS;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_BUNDLE_NOT_FOUND_BY_ID;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_NOT_FOUND;
import static com.nt.course_service_lms.constants.CourseBundleConstants.FAILED_TO_CREATE_COURSE_BUNDLE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.FAILED_TO_DELETE_COURSE_BUNDLE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.FAILED_TO_FETCH_COURSE_BUNDLE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID;
import static com.nt.course_service_lms.constants.CourseBundleConstants.FAILED_TO_UPDATE_COURSE_BUNDLE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.INVALID_BUNDLE_ID;
import static com.nt.course_service_lms.constants.CourseBundleConstants.INVALID_COURSE_ID;
import static com.nt.course_service_lms.constants.CourseBundleConstants.NO_COURSE_BUNDLES_FOUND;
import static com.nt.course_service_lms.converters.CourseBundleConvertor.convertDTOToEntityPost;
import static com.nt.course_service_lms.converters.CourseBundleConvertor.convertEntityToDTO;

/**
 * Implementation of the {@link CourseBundleService} interface that handles operations related to course-bundle mappings.
 * <p>
 * This service provides comprehensive functionality for
 * managing course-bundle associations in a Learning Management System (LMS).
 * It supports creating, updating, deleting, and retrieving course-bundle records with proper validation and exception handling.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *   <li>CRUD operations on course-bundle mappings</li>
 *   <li>Bundle information retrieval with course counts</li>
 *   <li>Course management within bundles (add/remove)</li>
 *   <li>Validation of bundle and course existence</li>
 *   <li>Comprehensive exception handling and logging</li>
 * </ul>
 * </p>
 *
 * @author Course Service Team
 * @version 1.0
 * @see CourseBundleService
 * @see CourseBundle
 * @see Bundle
 * @see Course
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseBundleServiceImpl implements CourseBundleService {

    /**
     * Repository for performing CRUD operations on {@link CourseBundle} entity.
     * <p>
     * This repository handles all database operations related to course-bundle mappings,
     * including creating, updating, deleting, and querying course-bundle associations.
     * </p>
     */
    @Autowired
    private final CourseBundleRepository courseBundleRepository;

    /**
     * Repository for performing CRUD operations on {@link Bundle} entity.
     * <p>
     * This repository manages bundle-related database operations and is used for
     * validating bundle existence and retrieving bundle information.
     * </p>
     */
    @Autowired
    private final BundleRepository bundleRepository;

    /**
     * Repository for performing CRUD operations on {@link Course} entity.
     * <p>
     * This repository handles course-related database operations and is used for
     * validating course existence and retrieving course information.
     * </p>
     */
    @Autowired
    private final CourseRepository courseRepository;

    /**
     * Retrieves all course-bundle mappings from the repository.
     * <p>
     * This method fetches all course-bundle associations and enriches them with
     * corresponding bundle and course names for comprehensive information display.
     * </p>
     *
     * @return a list of {@link CourseBundleOutDTO} objects representing all course-bundle mappings,
     * each containing course-bundle ID, bundle ID, course ID, bundle name, course name,
     * and active status
     * @throws ResourceNotFoundException if no course-bundle records are found in the database
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     *                                   or data processing
     * @see CourseBundleOutDTO
     * @see CourseBundle
     */
    @Override
    public List<CourseBundleOutDTO> getAllCourseBundles() {
        log.info("Fetching all course-bundle records");
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findAll();

            // Check if no records are found
            if (courseBundles.isEmpty()) {
                log.warn("No course-bundle records found");
                throw new ResourceNotFoundException(NO_COURSE_BUNDLES_FOUND);
            }

            // Convert entities to DTOs using the helper method
            // noinspection unused
            List<CourseBundleOutDTO> courseBundleOutDTOS = new ArrayList<>();
            for (CourseBundle courseBundle : courseBundles) {
                // noinspection unused
                CourseBundleOutDTO courseBundleOutDTO = convertEntityToDTO(courseBundle);
                Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
                if (bundle.isEmpty()) {
                    throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
                }
                String bundleName = bundle.get().getBundleName();
                courseBundleOutDTO.setBundleName(bundleName);

                Optional<Course> course = courseRepository.findById(courseBundle.getBundleId());
                if (course.isEmpty()) {
                    throw new ResourceNotFoundException(COURSE_NOT_FOUND);
                }
                String courseName = course.get().getTitle();
                courseBundleOutDTO.setCourseName(courseName);

                courseBundleOutDTOS.add(courseBundleOutDTO);
            }

            log.info("Successfully retrieved {} course-bundle records", courseBundleOutDTOS.size());
            return courseBundleOutDTOS;

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching all records: {}", ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE, ex);
        }
    }

    /**
     * Retrieves a specific course-bundle mapping by its unique identifier.
     * <p>
     * This method fetches a single course-bundle association and enriches it with
     * corresponding bundle and course names for comprehensive information display.
     * </p>
     *
     * @param courseBundleId the unique identifier of the course-bundle mapping to retrieve.
     *                       Must be a valid, non-null Long value representing an existing
     *                       course-bundle record ID
     * @return the {@link CourseBundleOutDTO} object representing the course-bundle mapping,
     * containing course-bundle ID, bundle ID, course ID, bundle name, course name,
     * and active status
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     *                                   in the database
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     *                                   or data processing
     * @see CourseBundleOutDTO
     * @see CourseBundle
     */
    @Override
    public CourseBundleOutDTO getCourseBundleById(final Long courseBundleId) {
        log.info("Fetching course-bundle record with ID: {}", courseBundleId);
        try {
            // noinspection unused
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Convert entity to DTO
            CourseBundleOutDTO courseBundleOutDTO = convertEntityToDTO(courseBundle);

            Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
            if (bundle.isEmpty()) {
                throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
            }
            String bundleName = bundle.get().getBundleName();
            courseBundleOutDTO.setBundleName(bundleName);

            Optional<Course> course = courseRepository.findById(courseBundle.getBundleId());
            if (course.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            String courseName = course.get().getTitle();
            courseBundleOutDTO.setCourseName(courseName);

            log.info("Successfully retrieved course-bundle record: {}", courseBundleOutDTO);
            return courseBundleOutDTO;

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching record with ID {}: {}", courseBundleId, ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID + courseBundleId, ex);
        }
    }

    /**
     * Deletes a course-bundle mapping by its unique identifier.
     * <p>
     * This method permanently removes a course-bundle association from the database.
     * The operation validates the existence of the record before deletion.
     * </p>
     *
     * @param courseBundleId the unique identifier of the course-bundle mapping to delete.
     *                       Must be a valid, non-null Long value representing an existing
     *                       course-bundle record ID
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     *                                   in the database
     * @throws RuntimeException          if an unexpected error occurs during the database deletion
     *                                   operation
     * @see CourseBundle
     */
    @Override
    public void deleteCourseBundle(final Long courseBundleId) {
        try {
            log.info("Starting process to delete course-bundle record with ID: {}", courseBundleId);
            // noinspection unused
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            courseBundleRepository.delete(courseBundle);
            log.info("Successfully deleted course-bundle record with ID: {}", courseBundleId);

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_DELETE_COURSE_BUNDLE + courseBundleId, e);
        }
    }

    /**
     * Updates an existing course-bundle mapping with new information.
     * <p>
     * This method modifies an existing course-bundle association with the provided data.
     * It validates the existence of the record before updating and automatically sets
     * the updated timestamp.
     * </p>
     *
     * @param courseBundleId          the unique identifier of the course-bundle mapping to update.
     *                                Must be a valid, non-null Long value representing an existing
     *                                course-bundle record ID
     * @param updateCourseBundleInDTO the updated data for the course-bundle mapping.
     *                                Must be a valid {@link UpdateCourseBundleInDTO} object containing
     *                                the new bundle ID, course ID, and active status
     * @return a success message string indicating the course bundle was updated successfully
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     *                                   in the database
     * @throws RuntimeException          if an unexpected error occurs during the database update
     *                                   operation
     * @see UpdateCourseBundleInDTO
     * @see CourseBundle
     */
    @Override
    public String updateCourseBundle(final Long courseBundleId, final UpdateCourseBundleInDTO updateCourseBundleInDTO) {
        try {
            log.info("Starting process to update course-bundle record with ID: {}", courseBundleId);
            // noinspection unused
            CourseBundle existingBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Update entity fields using the DTO
            existingBundle.setBundleId(updateCourseBundleInDTO.getBundleId());
            existingBundle.setCourseId(updateCourseBundleInDTO.getCourseId());
            existingBundle.setUpdatedAt(LocalDateTime.now());
            existingBundle.setActive(updateCourseBundleInDTO.isActive());

            // Save updated entity to the database
            CourseBundle updatedBundle = courseBundleRepository.save(existingBundle);
            log.info("Successfully updated course-bundle record with ID: {}", updatedBundle.getCourseBundleId());

            // Convert updated entity to DTO
            return "Course Bundle Updated Successfully";

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while updating record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_UPDATE_COURSE_BUNDLE + courseBundleId, e);
        }
    }

    /**
     * Creates a new course-bundle mapping in the system.
     * <p>
     * This method establishes a new association between a course and a bundle.
     * It performs comprehensive validation to ensure the bundle and course exist,
     * and that the association doesn't already exist before creation.
     * </p>
     *
     * @param courseBundleInDTO the data for the new course-bundle mapping.
     *                          Must be a valid {@link CourseBundleInDTO} object containing
     *                          the bundle ID, course ID, and active status
     * @return the created {@link CourseBundle} entity with generated ID and timestamps
     * @throws ResourceAlreadyExistsException if a course-bundle mapping already exists for the
     *                                        given bundle and course combination
     * @throws ResourceNotValidException      if the provided bundle ID or course ID is invalid
     *                                        or doesn't exist in the database
     * @throws RuntimeException               if an unexpected error occurs during the creation process
     * @see CourseBundleInDTO
     * @see CourseBundle
     * @see Bundle
     * @see Course
     */
    @Override
    public CourseBundle createCourseBundle(final CourseBundleInDTO courseBundleInDTO) {
        try {
            log.info("Starting process to create a new course-bundle mapping");

            // Check if the resource already exists
            if (courseBundleRepository.existsByBundleIdAndCourseId(courseBundleInDTO.getBundleId(),
                    courseBundleInDTO.getCourseId())) {
                log.error("Course-bundle mapping already exists for Bundle ID: {} and Course ID: {}",
                        courseBundleInDTO.getBundleId(), courseBundleInDTO.getCourseId());
                throw new ResourceAlreadyExistsException(COURSE_BUNDLE_ALREADY_EXISTS);
            }

            // Validate Bundle ID
            if (!bundleRepository.existsById(courseBundleInDTO.getBundleId())) {
                log.error("Invalid Bundle ID: {}", courseBundleInDTO.getBundleId());
                throw new ResourceNotValidException(INVALID_BUNDLE_ID + courseBundleInDTO.getBundleId());
            }

            // Validate Course ID
            if (!courseRepository.existsById(courseBundleInDTO.getCourseId())) {
                log.error("Invalid Course ID: {}", courseBundleInDTO.getCourseId());
                throw new ResourceNotValidException(INVALID_COURSE_ID + courseBundleInDTO.getCourseId());
            }

            // Convert DTO to Entity
            CourseBundle courseBundle = convertDTOToEntityPost(courseBundleInDTO);
            courseBundle.setCreatedAt(LocalDateTime.now());
            courseBundle.setUpdatedAt(LocalDateTime.now());
            // Save entity to the database
            CourseBundle savedBundle = courseBundleRepository.save(courseBundle);
            log.info("Successfully created a new course-bundle mapping with ID: {}", savedBundle.getCourseBundleId());

            // Convert saved entity to DTO
            return savedBundle;

        } catch (ResourceAlreadyExistsException | ResourceNotValidException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException(FAILED_TO_CREATE_COURSE_BUNDLE, e);
        }
    }

    /**
     * Retrieves information about all bundles, including their IDs, names, total courses, and active status.
     * <p>
     * This method provides comprehensive information about all bundles in the system,
     * including the count of courses associated with each bundle.
     * </p>
     *
     * @return a list of {@link BundleInfoOutDTO} objects containing bundle information,
     * including bundle ID, name, total course count, active status, and timestamps
     * @throws ResourceNotFoundException if no bundles are found in the database
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     *                                   or data processing
     * @see BundleInfoOutDTO
     * @see Bundle
     */
    @Override
    public List<BundleInfoOutDTO> getBundlesInfo() {
        try {
            List<Bundle> courseBundles = bundleRepository.findAll();
            if (courseBundles.isEmpty()) {
                throw new ResourceNotFoundException("No courses added in bundle");
            }
            List<BundleInfoOutDTO> bundleInfoOutDTOS = new ArrayList<>();
            for (Bundle courseBundle : courseBundles) {
                BundleInfoOutDTO bundleInfoOutDTO = new BundleInfoOutDTO();
                Bundle bundle = bundleRepository.findById(courseBundle.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bundle not found"));
                Long countCoursesInBundle = courseBundleRepository.countByBundleId(bundle.getBundleId());
                bundleInfoOutDTO.setBundleId(courseBundle.getBundleId());
                bundleInfoOutDTO.setBundleName(bundle.getBundleName());
                bundleInfoOutDTO.setTotalCourses(countCoursesInBundle);
                bundleInfoOutDTO.setActive(bundle.isActive());
                bundleInfoOutDTO.setCreatedAt(bundle.getCreatedAt());
                bundleInfoOutDTO.setUpdatedAt(bundle.getUpdatedAt());
                bundleInfoOutDTOS.add(bundleInfoOutDTO);
            }
            return bundleInfoOutDTOS;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    /**
     * Retrieves all courses belonging to a specific bundle.
     * <p>
     * This method fetches all course information associated with a given bundle,
     * providing details about each course including its ID, title, level, and active status.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle.
     *                 Must be a valid, non-null Long value representing an existing bundle ID
     * @return a list of {@link CourseInfoOutDTO} objects for the specified bundle,
     * containing course ID, title, level, and active status
     * @throws ResourceNotFoundException if no courses are found in the given bundle
     *                                   or if the bundle doesn't exist
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     *                                   or data processing
     * @see CourseInfoOutDTO
     * @see CourseBundle
     * @see Course
     */
    @Override
    public List<CourseInfoOutDTO> getAllCoursesByBundle(final Long bundleId) {
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findByBundleId(bundleId);
            if (courseBundles.isEmpty()) {
                throw new ResourceNotFoundException("No courses in the bundle");
            }
            List<CourseInfoOutDTO> coursesInfo = new ArrayList<>();
            for (CourseBundle courseBundle : courseBundles) {
                Optional<Course> course = courseRepository.findById(courseBundle.getCourseId());
                if (course.isPresent()) {
                    CourseInfoOutDTO courseInfoOutDTO = new CourseInfoOutDTO();
                    courseInfoOutDTO.setCourseId(course.get().getCourseId());
                    courseInfoOutDTO.setTitle(course.get().getTitle());
                    courseInfoOutDTO.setCourseLevel(course.get().getLevel());
                    courseInfoOutDTO.setActive(course.get().isActive());
                    coursesInfo.add(courseInfoOutDTO);
                }
            }
            return coursesInfo;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException("Something went wrong");
        }
    }

    /**
     * Retrieves the 5 most recent bundles along with their course counts.
     * <p>
     * This method provides a summary view of the most recently created bundles,
     * including their associated course counts for dashboard or overview purposes.
     * </p>
     *
     * @return a list of {@link BundleSummaryOutDTO} objects containing bundle summaries,
     * limited to the 5 most recent bundles ordered by creation date,
     * each including bundle ID, name, course count, and timestamps
     * @throws RuntimeException if an unexpected error occurs during the database operation
     *                          or data processing
     * @see BundleSummaryOutDTO
     * @see Bundle
     */
    @Override
    public List<BundleSummaryOutDTO> getRecentBundleSummaries() {
        // Get the 5 most recent bundles
        List<Bundle> recentBundles = bundleRepository.findTop5ByOrderByCreatedAtDesc();

        // Create the DTOs with course counts
        return recentBundles.stream()
                .map(bundle -> {
                    long courseCount = courseBundleRepository.countByBundleId(bundle.getBundleId());
                    return new BundleSummaryOutDTO(
                            bundle.getBundleId(),
                            bundle.getBundleName(),
                            courseCount,
                            bundle.getCreatedAt(),
                            bundle.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the IDs of all courses associated with a specific bundle.
     * <p>
     * This method returns only the course IDs for a given bundle, which is useful
     * for operations that need to work with course references without full course details.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle.
     *                 Must be a valid, non-null Long value representing an existing bundle ID
     * @return a list of Long values representing the course IDs associated with the specified bundle
     * @throws ResourceNotFoundException if no course IDs are found for the given bundle ID
     *                                   or if the bundle doesn't exist
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     * @see CourseBundle
     */
    @Override
    public List<Long> findCourseIdsByBundleId(final Long bundleId) {
        try {
            List<Long> courseIds = courseBundleRepository.findCourseIdsByBundleId(bundleId);
            if (courseIds.isEmpty()) {
                throw new ResourceNotFoundException("No Course IDs found");
            }
            return courseIds;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("SERVER ERROR");
        }
    }

    /**
     * Retrieves all courses that can be added to a specific bundle.
     * <p>
     * This method returns all active courses that are not currently associated
     * with the specified bundle, allowing users to see which courses are available
     * for addition to the bundle.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle.
     *                 Must be a valid, non-null Long value representing an existing bundle ID
     * @return a list of {@link CourseInfoOutDTO} objects representing courses that can be added
     * to the bundle, including only active courses not already in the bundle
     * @throws RuntimeException if an unexpected error occurs during the database operation
     *                          or data processing
     * @see CourseInfoOutDTO
     * @see Course
     * @see CourseBundle
     */
    @Override
    public List<CourseInfoOutDTO> getCoursesToAdd(final Long bundleId) {
        List<CourseBundle> courseBundles = courseBundleRepository.findByBundleId(bundleId);

        Set<Long> coursesIds = courseBundles.stream()
                .filter(CourseBundle::isActive)
                .map(CourseBundle::getCourseId)
                .collect(Collectors.toSet()
                );

        List<Course> courses = courseRepository.findAll();
        List<CourseInfoOutDTO> courseToAdd = courses.stream().filter(course ->
                        !coursesIds.contains(course.getCourseId()) && course.isActive())
                .map(CourseConvertors::courseToCourseInfoOutDTO).collect(Collectors.toList());

        return courseToAdd;
    }

    /**
     * Adds multiple courses to a specific bundle.
     * <p>
     * This method allows adding multiple courses to a bundle in a single operation.
     * It handles both new associations (creating new records) and reactivating
     * previously deactivated associations.
     * </p>
     *
     * @param addCourseToBundleInDTO the data transfer object containing the bundle ID
     *                               and list of course IDs to add.
     *                               Must be a valid {@link AddCourseToBundleInDTO} object
     *                               with a valid bundle ID and non-empty course list
     * @return a {@link StandardResponseOutDTO} containing a success message and operation details
     * @throws ResourceNotFoundException if the specified bundle is not found in the database
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     * @see AddCourseToBundleInDTO
     * @see StandardResponseOutDTO
     * @see MessageOutDTO
     * @see CourseBundle
     * @see Bundle
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> addCourseToBundle(final AddCourseToBundleInDTO addCourseToBundleInDTO) {
        Optional<Bundle> bundle = bundleRepository.findById(addCourseToBundleInDTO.getBundleId());
        if (bundle.isPresent()) {
            Long bundleId = bundle.get().getBundleId();

            for (Long courseId : addCourseToBundleInDTO.getCourses()) {
                if (!courseBundleRepository.existsByBundleIdAndCourseId(bundleId, courseId)) {
                    CourseBundle courseBundle = new CourseBundle();
                    courseBundle.setCourseId(courseId);
                    courseBundle.setBundleId(bundleId);
                    courseBundle.setActive(true);
                    courseBundle.setCreatedAt(LocalDateTime.now());
                    courseBundle.setUpdatedAt(LocalDateTime.now());
                    courseBundleRepository.save(courseBundle);
                } else {
                    Optional<CourseBundle> coursebundle = courseBundleRepository.findByBundleIdAndCourseId(bundleId, courseId);
                    coursebundle.get().setActive(true);
                    courseBundleRepository.save(coursebundle.get());

                }
            }

        } else {
            log.warn("Bundle with the given id {} not found", addCourseToBundleInDTO.getBundleId());
            throw new ResourceNotFoundException("Bundle not found");
        }
        MessageOutDTO message = new MessageOutDTO("Courses added to bundle");
        return StandardResponseOutDTO.success(message, "Course added to Bundle.");
    }

    /**
     * Removes a specific course from a bundle by deactivating the association.
     * <p>
     * This method performs a soft delete by setting the active status to false
     * rather than permanently deleting the course-bundle association record.
     * This allows for potential reactivation in the future.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle.
     *                 Must be a valid, non-null Long value representing an existing bundle ID
     * @param courseId the unique identifier of the course to remove.
     *                 Must be a valid, non-null Long value representing an existing course ID
     * @return a {@link StandardResponseOutDTO} containing a success message and operation details
     * @throws ResourceNotFoundException if the course is not found in the specified bundle
     *                                   or if the association doesn't exist
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     * @see StandardResponseOutDTO
     * @see MessageOutDTO
     * @see CourseBundle
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> removeCourse(final Long bundleId, final Long courseId) {
        Optional<CourseBundle> courseBundle = courseBundleRepository.findByBundleIdAndCourseId(bundleId, courseId);
        if (courseBundle.isPresent()) {
            courseBundle.get().setActive(false);
            courseBundleRepository.save(courseBundle.get());
        } else {
            log.warn("Course not present in the bundle");
            throw new ResourceNotFoundException("Course not found in bundle");
        }
        MessageOutDTO message = new MessageOutDTO("Course Removed");
        return StandardResponseOutDTO.success(message, "Course Removed");
    }

    /**
     * Retrieves all courses associated with a specific bundle, including both active and inactive associations.
     * <p>
     * This method returns comprehensive course information for a bundle, showing all courses
     * regardless of their active status within the bundle. The active status in the response
     * reflects the course-bundle association status, not the course's individual active status.
     * </p>
     *
     * @param bundleId the unique identifier of the bundle.
     *                 Must be a valid, non-null Long value representing an existing bundle ID
     * @return a {@link StandardResponseOutDTO} containing a list of {@link CourseInfoOutDTO} objects
     * with course details and their association status within the bundle
     * @throws ResourceNotFoundException if the specified bundle is not found in the database
     * @throws RuntimeException          if an unexpected error occurs during the database operation
     *                                   or data processing
     * @see StandardResponseOutDTO
     * @see CourseInfoOutDTO
     * @see Bundle
     * @see CourseBundle
     * @see Course
     */
    @Override
    public StandardResponseOutDTO<List<CourseInfoOutDTO>> getBundleCourses(final Long bundleId) {
        if (!bundleRepository.existsById(bundleId)) {
            throw new ResourceNotFoundException("Bundle not found");
        }
        List<CourseInfoOutDTO> courseInfo = new ArrayList<>();
        List<CourseBundle> courseBundles = courseBundleRepository.findByBundleId(bundleId);
        for (CourseBundle courseBundle : courseBundles) {
            Optional<Course> course = courseRepository.findById(courseBundle.getCourseId());
            if (course.isPresent()) {
                CourseInfoOutDTO courseInfoOutDTO = new CourseInfoOutDTO();
                courseInfoOutDTO.setCourseId(course.get().getCourseId());
                courseInfoOutDTO.setCourseLevel(course.get().getLevel());
                courseInfoOutDTO.setTitle(course.get().getTitle());
                courseInfoOutDTO.setActive(courseBundle.isActive());
                courseInfo.add(courseInfoOutDTO);
            }
        }

        return StandardResponseOutDTO.success(courseInfo, "Courses fetched.");
    }

}
