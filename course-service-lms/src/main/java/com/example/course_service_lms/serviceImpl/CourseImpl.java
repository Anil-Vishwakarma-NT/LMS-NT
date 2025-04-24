package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.CourseConvertors;
import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.dto.CourseSummaryDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseLevel;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.CourseBundleRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseService;
import com.example.course_service_lms.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_ALREADY_EXISTS;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_DELETED_SUCCESSFULLY;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_DUPLICATE_FOR_OWNER;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_UPDATED_SUCCESSFULLY;

/**
 * Implementation of the {@link CourseService} interface that manages course-related operations.
 * Supports creating, retrieving, updating, and deleting course records.
 * Includes validation logic and logs significant operations.
 *
 * @see CourseService
 */
@Slf4j
@Service
public class CourseImpl implements CourseService {

    /**
     * Repository to interact with course data.
     */
    @Autowired
    private CourseRepository courseRepository;

    /**
     * Repository to manage course-bundle mapping data.
     */
    @Autowired
    private CourseBundleRepository courseBundleRepository;

    /**
     * Creates a new course after checking for duplicates.
     *
     * @param courseDTO the course data transfer object containing course details
     * @return the saved {@link Course} entity
     * @throws ResourceAlreadyExistsException if a course with the same title and owner already exists
     */
    @Override
    public Course createCourse(final CourseDTO courseDTO) {
        try {
            log.info("Creating a new course with title: {}", courseDTO.getTitle());

            Optional<Course> existingCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(
                    courseDTO.getTitle(), courseDTO.getOwnerId());
            if (existingCourse.isPresent()) {
                log.warn("Course with title '{}' already exists.", courseDTO.getTitle());
                throw new ResourceAlreadyExistsException(COURSE_ALREADY_EXISTS);
            }

            Course course = CourseConvertors.courseDtoToCourse(courseDTO);
            Course savedCourse = courseRepository.save(course);
            log.info("Course '{}' created successfully with ID: {}", courseDTO.getTitle(), savedCourse.getCourseId());
            return savedCourse;
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Retrieves all courses from the repository.
     *
     * @return a list of all {@link Course} entities
     * @throws ResourceNotFoundException if no courses are found
     */
    @Override
    public List<Course> getAllCourses() {
        try {
            log.info("Fetching all courses...");
            List<Course> courses = courseRepository.findAll();
            if (courses.isEmpty()) {
                log.warn("No courses found.");
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            log.info("Found {} courses.", courses.size());
            return courses;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param courseId the ID of the course to retrieve
     * @return an {@link Optional} containing the found {@link Course}, or empty if not found
     * @throws ResourceNotFoundException if the course does not exist
     */
    @Override
    public Optional<Course> getCourseById(final Long courseId) {
        try {
            log.info("Fetching course by ID: {}", courseId);
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            log.info("Course found: {}", course.get().getTitle());
            return course;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Deletes a course by its ID.
     *
     * @param courseId the ID of the course to delete
     * @return a confirmation message
     * @throws ResourceNotFoundException if the course does not exist
     */
    @Override
    public String deleteCourse(final Long courseId) {
        try {
            log.info("Deleting course with ID: {}", courseId);
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            courseRepository.delete(course.get());
            log.info("Course with ID: {} deleted successfully.", courseId);
            return COURSE_DELETED_SUCCESSFULLY;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Updates an existing course.
     * Checks for duplicate title and owner combinations before updating.
     *
     * @param courseId  the ID of the course to update
     * @param courseDTO the updated course details
     * @return a confirmation message
     * @throws ResourceNotFoundException if the course to update is not found
     * @throws ResourceNotValidException if the updated title and owner combination already exists for another course
     */
    @Override
    public String updateCourse(final Long courseId, final CourseDTO courseDTO) {
        try {
            log.info("Updating course with ID: {}", courseId);

            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if (courseOptional.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }

            Course existingCourse = courseOptional.get();

            boolean isTitleChanged = !existingCourse.getTitle().equalsIgnoreCase(courseDTO.getTitle());
            boolean isOwnerChanged = !(existingCourse.getOwnerId() == courseDTO.getOwnerId());

            if (isTitleChanged || isOwnerChanged) {
                Optional<Course> duplicateCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(
                        courseDTO.getTitle(), courseDTO.getOwnerId()
                );

                if (duplicateCourse.isPresent() && !(duplicateCourse.get().getCourseId() == courseId)) {
                    log.warn("Duplicate course title '{}' exists for owner ID {}", courseDTO.getTitle(), courseDTO.getOwnerId());
                    throw new ResourceNotValidException(COURSE_DUPLICATE_FOR_OWNER);
                }
            }

            existingCourse.setTitle(StringUtils.toProperCase(courseDTO.getTitle()));
            existingCourse.setDescription(StringUtils.toProperCase(courseDTO.getDescription()));
            existingCourse.setLevel(CourseLevel.valueOf(courseDTO.getCourseLevel()));
            existingCourse.setOwnerId(courseDTO.getOwnerId());

            courseRepository.save(existingCourse);
            log.info("Course with ID: {} updated successfully.", courseId);
            return COURSE_UPDATED_SUCCESSFULLY;
        } catch (ResourceNotFoundException | ResourceNotValidException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Checks if a course exists by its ID.
     * This method is specifically used by the User microservice.
     *
     * @param courseId the ID of the course
     * @return true if the course exists, false otherwise
     */
    @Override
    public boolean courseExistsById(final Long courseId) {
        return courseRepository.existsById(courseId);
    }

    @Override
    public long countCourses() {
        return courseRepository.count();
    }

    @Override
    public List<CourseSummaryDTO> getRecentCourseSummaries() {
        List<Object[]> results = courseRepository.fetchRecentCourseSummaries();

        return results.stream().map(obj -> {
            CourseSummaryDTO dto = new CourseSummaryDTO();
            dto.setTitle((String) obj[0]);
            dto.setDescription((String) obj[1]);
            dto.setLevel(CourseLevel.valueOf((String) obj[2]));
            return dto;
        }).collect(Collectors.toList());

    }
}
