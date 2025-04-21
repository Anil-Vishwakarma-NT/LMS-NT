package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.CourseConvertors;
import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseBundle;
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
import java.util.Set;

import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.CourseConstants.*;

@Slf4j
@Service
public class CourseImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseBundleRepository courseBundleRepository;

    @Override
    public Course createCourse(CourseDTO courseDTO) {
        try {
            log.info("Creating a new course with title: {}", courseDTO.getTitle());

            Optional<Course> existingCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(courseDTO.getTitle(),courseDTO.getOwnerId());
            if (existingCourse.isPresent()) {
                log.warn("Course with title '{}' already exists.", courseDTO.getTitle());
                throw new ResourceAlreadyExistsException(COURSE_ALREADY_EXISTS);
            }

            Course course = CourseConvertors.courseDtoToCourse(courseDTO);
            Course savedCourse = courseRepository.save(course);
            log.info("Course '{}' created successfully with ID: {}", courseDTO.getTitle(), savedCourse.getCourseId());
            return savedCourse;
        } catch(ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

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

    @Override
    public Optional<Course> getCourseById(Long courseId) {
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

    @Override
    public String deleteCourse(Long courseId) {
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

    @Override
    public String updateCourse(Long courseId, CourseDTO courseDTO) {
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

    //Methods required specifically for user microservice
    @Override
    public boolean courseExistsById(Long courseId) {
        return courseRepository.existsById(courseId);
    }
    @Override
    public long countCourses() {
        return courseRepository.count();
    }


}
