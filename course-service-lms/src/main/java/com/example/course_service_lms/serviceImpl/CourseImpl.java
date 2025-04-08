package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.Enum.CourseLevel;
import com.example.course_service_lms.converters.CourseConvertors;
import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseService;
import com.example.course_service_lms.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CourseImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public Course createCourse(CourseDTO courseDTO) {
        try {
            log.info("Creating a new course with title: {}", courseDTO.getTitle());

            Optional<Course> existingCourse = courseRepository.findByTitleIgnoreCaseAndOwnerId(courseDTO.getTitle(),courseDTO.getOwnerId());
            if (existingCourse.isPresent()) {
                log.warn("Course with title '{}' already exists.", courseDTO.getTitle());
                throw new ResourceAlreadyExistsException("A course with this name already exists");
            }

            Course course = CourseConvertors.courseDtoToCourse(courseDTO);
            Course savedCourse = courseRepository.save(course);
            log.info("Course '{}' created successfully with ID: {}", courseDTO.getTitle(), savedCourse.getCourseId());
            return savedCourse;
        } catch(ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public List<Course> getAllCourses() {
        try {
            log.info("Fetching all courses...");
            List<Course> courses = courseRepository.findAll();
            if (courses.isEmpty()) {
                log.warn("No courses found.");
                throw new ResourceNotFoundException("No Courses Found");
            }
            log.info("Found {} courses.", courses.size());
            return courses;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        try {
            log.info("Fetching course by ID: {}", courseId);
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException("No Course Found");
            }
            log.info("Course found: {}", course.get().getTitle());
            return course;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public String deleteCourse(Long courseId) {
        try {
            log.info("Deleting course with ID: {}", courseId);
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException("No Course Found");
            }
            courseRepository.delete(course.get());
            log.info("Course with ID: {} deleted successfully.", courseId);
            return "Course Deleted Successfully";
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public String updateCourse(Long courseId, CourseDTO courseDTO) {
        try {
            log.info("Updating course with ID: {}", courseId);

            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if (courseOptional.isEmpty()) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException("No Course Found");
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
                    throw new ResourceNotValidException("A course with this title already exists for this owner");
                }
            }

            existingCourse.setTitle(StringUtils.toProperCase(courseDTO.getTitle()));
            existingCourse.setDescription(StringUtils.toProperCase(courseDTO.getDescription()));
            existingCourse.setLevel(CourseLevel.valueOf(courseDTO.getCourseLevel()));
            existingCourse.setOwnerId(courseDTO.getOwnerId());

            courseRepository.save(existingCourse);
            log.info("Course with ID: {} updated successfully.", courseId);
            return "Course Updated Successfully";
        } catch (ResourceNotFoundException | ResourceNotValidException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

}
