package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.converters.CourseConvertors;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.DashboardDataOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.CourseBundleRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nt.course_service_lms.constants.CourseConstants.COURSE_ALREADY_EXISTS;
import static com.nt.course_service_lms.constants.CourseConstants.COURSE_DELETED_SUCCESSFULLY;
import static com.nt.course_service_lms.constants.CourseConstants.COURSE_DUPLICATE_FOR_OWNER;
import static com.nt.course_service_lms.constants.CourseConstants.COURSE_NOT_FOUND;

/**
 * Implementation of the {@link CourseService} interface that manages course-related operations
 * in the Learning Management System (LMS).
 *
 * <p>This service provides comprehensive course management functionality including:
 * <ul>
 *   <li>Creating new courses with validation</li>
 *   <li>Retrieving courses by various criteria</li>
 *   <li>Updating existing course information</li>
 *   <li>Deleting courses</li>
 *   <li>Validating course existence and uniqueness</li>
 * </ul>
 *
 * <p>The service ensures data integrity by preventing duplicate course titles
 * for the same owner and provides comprehensive logging for all operations.
 *
 * @author NT Development Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    /**
     * Repository for performing CRUD operations on Course entities.
     * Provides access to course data persistence layer.
     */
    @Autowired
    private CourseRepository courseRepository;

    /**
     * Repository for managing course bundle relationships.
     * Used for operations involving course bundles and packages.
     */
    @Autowired
    private CourseBundleRepository courseBundleRepository;

    /**
     * Creates a new course in the system.
     *
     * <p>This method validates that no course with the same title exists for the given owner,
     * sets creation and update timestamps, and persists the course to the database.
     *
     * @param courseInDTO the course data transfer object containing course information
     * @return {@link CourseOutDTO} containing the created course details
     * @throws ResourceAlreadyExistsException if a course with the same title already exists for the owner
     * @throws IllegalArgumentException       if courseInDTO is null or contains invalid data
     * @since 1.0
     */
    @Override
    public CourseOutDTO createCourse(final CourseInDTO courseInDTO) {
        log.info("Creating course with title: '{}'", courseInDTO.getTitle());

        validateCourseDoesNotExist(courseInDTO.getTitle(), courseInDTO.getOwnerId());

        Course course = CourseConvertors.courseInDTOToCourse(courseInDTO);
        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        Course savedCourse = courseRepository.save(course);
        CourseOutDTO courseOutDTO = CourseConvertors.courseToCourseOutDTO(savedCourse);
        log.info("Course '{}' created successfully with ID: {}",
                courseInDTO.getTitle(), savedCourse.getCourseId());
        return courseOutDTO;
    }

    /**
     * Retrieves all courses from the system.
     *
     * <p>This method fetches all available courses and converts them to DTOs for client consumption.
     *
     * @return {@link List} of {@link CourseOutDTO} containing all courses
     * @throws ResourceNotFoundException if no courses are found in the system
     * @since 1.0
     */
    @Override
    public List<CourseOutDTO> getAllCourses() {
        log.info("Fetching all courses");
        List<Course> courses = courseRepository.findAll();

        if (courses.isEmpty()) {
            log.warn("No courses found");
            throw new ResourceNotFoundException(COURSE_NOT_FOUND);
        }

        List<CourseOutDTO> courseOutDTOS = courses.stream()
                .map(CourseConvertors::courseToCourseOutDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} courses", courses.size());
        return courseOutDTOS;
    }

    /**
     * Retrieves detailed information for a specific course by its ID.
     *
     * <p>This method provides comprehensive course information including all course details.
     *
     * @param courseId the unique identifier of the course to retrieve
     * @return {@link CourseInfoOutDTO} containing detailed course information
     * @throws ResourceNotFoundException if no course is found with the given ID
     * @throws IllegalArgumentException  if courseId is null or invalid
     * @since 1.0
     */
    @Override
    public CourseInfoOutDTO getCourseById(final Long courseId) {
        log.info("Fetching course by ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);
        log.info("Course found: '{}'", course.getTitle());

        CourseInfoOutDTO courseOutDTO = CourseConvertors.courseToCourseInfoOutDTO(course);
        return courseOutDTO;
    }

    /**
     * Retrieves the name/title of a course by its ID.
     *
     * <p>This is a lightweight method that returns only the course title without other details.
     *
     * @param courseId the unique identifier of the course
     * @return {@link String} containing the course title
     * @throws ResourceNotFoundException if no course is found with the given ID
     * @throws IllegalArgumentException  if courseId is null or invalid
     * @since 1.0
     */
    @Override
    public String getCourseNameById(final Long courseId) {
        log.info("Fetching course name by ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);
        log.info("Course name retrieved: '{}'", course.getTitle());
        return course.getTitle();
    }

    /**
     * Deletes a course from the system.
     *
     * <p>This method permanently removes a course from the database. The operation is irreversible.
     *
     * @param courseId the unique identifier of the course to delete
     * @return {@link String} confirmation message indicating successful deletion
     * @throws ResourceNotFoundException if no course is found with the given ID
     * @throws IllegalArgumentException  if courseId is null or invalid
     * @since 1.0
     */
    @Override
    public String deleteCourse(final Long courseId) {
        log.info("Deleting course with ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);

        courseRepository.delete(course);
        log.info("Course '{}' with ID: {} deleted successfully", course.getTitle(), courseId);
        return COURSE_DELETED_SUCCESSFULLY;
    }

    /**
     * Retrieves detailed information for all courses in the system.
     *
     * <p>This method returns comprehensive information about all courses, including metadata
     * that may not be included in the basic course listing.
     *
     * @return {@link List} of {@link CourseInfoOutDTO} containing detailed information for all courses
     * @throws ResourceNotFoundException if no courses are found in the system
     * @since 1.0
     */
    @Override
    public List<CourseInfoOutDTO> getCoursesInfo() {
        log.info("Fetching course information");
        List<Course> courses = courseRepository.findAll();

        if (courses.isEmpty()) {
            log.warn("No courses found for course info");
            throw new ResourceNotFoundException(COURSE_NOT_FOUND);
        }

        List<CourseInfoOutDTO> courseDTOs = courses.stream()
                .map(CourseConvertors::courseToCourseInfoOutDTO)
                .collect(Collectors.toList());

        log.info("Retrieved info for {} courses", courseDTOs.size());
        return courseDTOs;
    }

    /**
     * Validates the existence of courses by their IDs and returns the list of existing course IDs.
     *
     * <p>This method is useful for bulk operations where you need to verify which courses
     * from a given list actually exist in the system.
     *
     * @param courseIds {@link List} of course IDs to validate
     * @return {@link List} of {@link Long} containing only the IDs of courses that exist
     * @throws ResourceNotFoundException if no courses are found with the given IDs
     * @throws RuntimeException          if a server error occurs during validation
     * @throws IllegalArgumentException  if courseIds is null or empty
     * @since 1.0
     */
    @Override
    public List<Long> findExistingIds(final List<Long> courseIds) {
        try {
            List<Long> existingCourseIds = courseRepository.findExistingIds(courseIds);
            if (existingCourseIds.isEmpty()) {
                throw new ResourceNotFoundException("No Course IDs found");
            }
            return existingCourseIds;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("SERVER ERROR");
        }
    }

    /**
     * Updates an existing course with new information.
     *
     * <p>This method validates that the update won't create duplicate title-owner combinations,
     * applies the updates, and sets the updated timestamp.
     *
     * @param courseId          the unique identifier of the course to update
     * @param updateCourseInDTO the DTO containing updated course information
     * @return {@link CourseOutDTO} containing the updated course details
     * @throws ResourceNotFoundException if no course is found with the given ID
     * @throws ResourceNotValidException if the update would create a duplicate course for the owner
     * @throws IllegalArgumentException  if courseId is null or updateCourseInDTO is null/invalid
     * @since 1.0
     */
    @Override
    public CourseOutDTO updateCourse(final Long courseId, final UpdateCourseInDTO updateCourseInDTO) {
        System.out.println();
        log.info("Updating course with ID: {}", courseId);

        Course existingCourse = findCourseByIdOrThrow(courseId);

        System.out.println(".........................................");

        validateNoDuplicateOnUpdate(courseId, updateCourseInDTO, existingCourse);

        CourseConvertors.updateCourseFromDTO(existingCourse, updateCourseInDTO);
        existingCourse.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(existingCourse);
        log.info("Course '{}' with ID: {} updated successfully",
                existingCourse.getTitle(), courseId);
        return CourseConvertors.courseToCourseOutDTO(existingCourse);
    }

    /**
     * Checks if a course exists in the system by its ID.
     *
     * <p>This is a lightweight method for existence validation without retrieving the full course data.
     *
     * @param courseId the unique identifier of the course to check
     * @return {@code true} if the course exists, {@code false} otherwise
     * @throws IllegalArgumentException if courseId is null
     * @since 1.0
     */
    @Override
    public boolean courseExistsById(final Long courseId) {
        log.debug("Checking if course exists with ID: {}", courseId);
        return courseRepository.existsById(courseId);
    }

    /**
     * Returns the total number of courses in the system.
     *
     * <p>This method provides a count of all courses without loading the actual course data.
     *
     * @return {@code long} representing the total number of courses
     * @since 1.0
     */
    @Override
    public long countCourses() {
        log.debug("Counting total courses");
        return courseRepository.count();
    }

    /**
     * Retrieves summaries of the most recently created courses.
     *
     * <p>This method returns a condensed view of the latest 5 courses, useful for dashboard
     * or preview functionality.
     *
     * @return {@link List} of {@link CourseSummaryOutDTO} containing summaries of recent courses
     * @throws ResourceNotFoundException if no courses are found in the system
     * @since 1.0
     */
    @Override
    public List<CourseSummaryOutDTO> getRecentCourseSummaries() {
        log.info("Fetching recent course summaries");
        List<Course> recentCourses = courseRepository.findTop5ByOrderByCreatedAtDesc();

        if (recentCourses == null || recentCourses.isEmpty()) {
            log.warn("No recent courses found");
            throw new ResourceNotFoundException("No courses found");
        }

        List<CourseSummaryOutDTO> summaries = recentCourses.stream()
                .map(CourseConvertors::courseToCourseSummaryOutDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} recent course summaries", summaries.size());
        return summaries;
    }

    @Override
    public DashboardDataOutDTO getRecentDashboardData() {
        log.info("Fetching recent dashboard data with optimized query");

        List<Object[]> results = courseRepository.findRecentDashboardData();

        List<CourseSummaryOutDTO> courseSummaries = new ArrayList<>();
        List<BundleSummaryOutDTO> bundleSummaries = new ArrayList<>();

        for (Object[] row : results) {
            String type = (String) row[0];
            if ("COURSE".equals(type)) {
                courseSummaries.add(CourseSummaryOutDTO.builder()
                        .title((String) row[2])
                        .description((String) row[3])
                        .level((String) row[4])
                        .createdAt(((Timestamp) row[5]).toLocalDateTime())
                        .updatedAt(((Timestamp) row[6]).toLocalDateTime())
                        .build());
            } else if ("BUNDLE".equals(type)) {
                bundleSummaries.add(BundleSummaryOutDTO.builder()
                        .bundleId(((Number) row[1]).longValue())
                        .bundleName((String) row[2])
                        .courseCount(((Number) row[7]).longValue())
                        .createdAt(((Timestamp) row[5]).toLocalDateTime())
                        .updatedAt(((Timestamp) row[6]).toLocalDateTime())
                        .build());
            }
        }

        log.info("Retrieved {} recent courses and {} recent bundles with optimized query",
                courseSummaries.size(), bundleSummaries.size());

        return DashboardDataOutDTO.builder()
                .recentCourses(courseSummaries)
                .recentBundles(bundleSummaries)
                .build();
    }

    // Private helper methods

    /**
     * Finds a course by its ID or throws a ResourceNotFoundException if not found.
     *
     * <p>This is a utility method used internally to retrieve courses and handle
     * the not-found scenario consistently across all methods.
     *
     * @param courseId the unique identifier of the course to find
     * @return {@link Course} entity if found
     * @throws ResourceNotFoundException if no course is found with the given ID
     * @throws IllegalArgumentException  if courseId is null
     */
    private Course findCourseByIdOrThrow(final Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID: {}", courseId);
                    return new ResourceNotFoundException(COURSE_NOT_FOUND);
                });
    }

    /**
     * Validates that no course exists with the given title and owner ID combination.
     *
     * <p>This method ensures uniqueness of course titles within the scope of a single owner.
     * The validation is case-insensitive for the title comparison.
     *
     * @param title   the course title to validate
     * @param ownerId the ID of the course owner
     * @throws ResourceAlreadyExistsException if a course with the same title already exists for the owner
     * @throws IllegalArgumentException       if title is null/empty or ownerId is null
     */
    private void validateCourseDoesNotExist(final String title, final Long ownerId) {
        Optional<Course> existingCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(title, ownerId);
        if (existingCourse.isPresent()) {
            log.warn("Course with title '{}' already exists for owner ID: {}", title, ownerId);
            throw new ResourceAlreadyExistsException(COURSE_ALREADY_EXISTS);
        }
    }

    /**
     * Validates that updating a course won't create a duplicate title-owner combination.
     *
     * <p>This method checks if the proposed update would result in a duplicate course
     * for the same owner. It only performs validation if the title or owner is actually changing.
     *
     * @param courseId       the ID of the course being updated
     * @param updateDTO      the DTO containing the proposed updates
     * @param existingCourse the current course entity
     * @throws ResourceNotValidException if the update would create a duplicate course for the owner
     * @throws IllegalArgumentException  if any parameter is null
     */
    private void validateNoDuplicateOnUpdate(
            final Long courseId,
            final UpdateCourseInDTO updateDTO,
            final Course existingCourse
    ) {
        boolean isTitleChanged = !existingCourse.getTitle().equalsIgnoreCase(updateDTO.getTitle());
        boolean isOwnerChanged = !(existingCourse.getOwnerId() == (updateDTO.getOwnerId()));

        if (isTitleChanged || isOwnerChanged) {
            Optional<Course> duplicateCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(
                    updateDTO.getTitle(), updateDTO.getOwnerId());

            if (duplicateCourse.isPresent() && !(duplicateCourse.get().getCourseId() == courseId)) {
                log.warn("Duplicate course title '{}' exists for owner ID: {}",
                        updateDTO.getTitle(), updateDTO.getOwnerId());
                throw new ResourceAlreadyExistsException(COURSE_DUPLICATE_FOR_OWNER);
            }
        }
    }


    @Override
    public StandardResponseOutDTO<List<CourseInfoOutDTO>> getCoursesByIds(List<Long> courseIds) {
        List<Course> courses = courseRepository.findByCourseIdIn(courseIds);
        List<CourseInfoOutDTO> courseInfo = courses.stream().map(CourseConvertors::courseToCourseInfoOutDTO).collect(Collectors.toList());

        return StandardResponseOutDTO.success(courseInfo, "courses retrienved");

    }
}
