package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.CourseConvertors;
import com.example.course_service_lms.inDTO.CourseInDTO;
import com.example.course_service_lms.outDTO.CourseOutDTO;
import com.example.course_service_lms.outDTO.CourseSummaryOutDTO;
import com.example.course_service_lms.outDTO.CourseInfoOutDTO;
import com.example.course_service_lms.inDTO.UpdateCourseInDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.CourseBundleRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.course_service_lms.constants.CourseConstants.*;

/**
 * Implementation of the {@link CourseService} interface that manages course-related operations.
 * Supports creating, retrieving, updating, and deleting course records.
 * Includes validation logic and logs significant operations.
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseBundleRepository courseBundleRepository;

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

    @Override
    public Optional<CourseOutDTO> getCourseById(final Long courseId) {
        log.info("Fetching course by ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);
        log.info("Course found: '{}'", course.getTitle());

        CourseOutDTO courseOutDTO = CourseConvertors.courseToCourseOutDTO(course);
        return Optional.of(courseOutDTO);
    }


    @Override
    public String getCourseNameById(Long courseId) {
        log.info("Fetching course name by ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);
        log.info("Course name retrieved: '{}'", course.getTitle());
        return course.getTitle();
    }

    @Override
    public String deleteCourse(final Long courseId) {
        log.info("Deleting course with ID: {}", courseId);
        Course course = findCourseByIdOrThrow(courseId);

        courseRepository.delete(course);
        log.info("Course '{}' with ID: {} deleted successfully", course.getTitle(), courseId);
        return COURSE_DELETED_SUCCESSFULLY;
    }

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

    @Override
    public CourseOutDTO updateCourse(final Long courseId, final UpdateCourseInDTO updateCourseInDTO) {
        log.info("Updating course with ID: {}", courseId);

        Course existingCourse = findCourseByIdOrThrow(courseId);

        validateNoDuplicateOnUpdate(courseId, updateCourseInDTO, existingCourse);

        CourseConvertors.updateCourseFromDTO(existingCourse, updateCourseInDTO);
        existingCourse.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(existingCourse);
        log.info("Course '{}' with ID: {} updated successfully",
                existingCourse.getTitle(), courseId);
        return CourseConvertors.courseToCourseOutDTO(existingCourse);
    }

    @Override
    public boolean courseExistsById(final Long courseId) {
        log.debug("Checking if course exists with ID: {}", courseId);
        return courseRepository.existsById(courseId);
    }

    @Override
    public long countCourses() {
        log.debug("Counting total courses");
        return courseRepository.count();
    }

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

    // Private helper methods

    /**
     * Finds a course by ID or throws ResourceNotFoundException if not found.
     */
    private Course findCourseByIdOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID: {}", courseId);
                    return new ResourceNotFoundException(COURSE_NOT_FOUND);
                });
    }

    /**
     * Validates that no course exists with the given title and owner ID.
     */
    private void validateCourseDoesNotExist(String title, Long ownerId) {
        Optional<Course> existingCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(title, ownerId);
        if (existingCourse.isPresent()) {
            log.warn("Course with title '{}' already exists for owner ID: {}", title, ownerId);
            throw new ResourceAlreadyExistsException(COURSE_ALREADY_EXISTS);
        }
    }

    /**
     * Validates that updating the course won't create a duplicate title-owner combination.
     */
    private void validateNoDuplicateOnUpdate(Long courseId, UpdateCourseInDTO updateDTO, Course existingCourse) {
        boolean isTitleChanged = !existingCourse.getTitle().equalsIgnoreCase(updateDTO.getTitle());
        boolean isOwnerChanged = !(existingCourse.getOwnerId() == (updateDTO.getOwnerId()));

        if (isTitleChanged || isOwnerChanged) {
            Optional<Course> duplicateCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(
                    updateDTO.getTitle(), updateDTO.getOwnerId());

            if (duplicateCourse.isPresent() && !(duplicateCourse.get().getCourseId() == courseId)) {
                log.warn("Duplicate course title '{}' exists for owner ID: {}",
                        updateDTO.getTitle(), updateDTO.getOwnerId());
                throw new ResourceNotValidException(COURSE_DUPLICATE_FOR_OWNER);
            }
        }
    }
}