package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.CourseContentConverters;
import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.dto.UpdateCourseContentDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.CourseContentConstants.CONTENT_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_CONTENT_ALREADY_PRESENT;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_CONTENT_DELETED;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_CONTENT_DUPLICATE;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_CONTENT_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_CONTENT_UPDATED;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseContentConstants.NO_COURSE_CONTENTS_FOUND;

/**
 * Implementation of the {@link CourseContentService} interface that handles operations related to course content.
 * Provides functionality for creating, updating, deleting, and retrieving course content records.
 * Validates inputs and handles exceptions appropriately.
 *
 * @see CourseContentService
 */
@Service
public class CourseContentImpl implements CourseContentService {

    /**
     * Repository to interact with CourseContent data source.
     */
    @Autowired
    private CourseContentRepository courseContentRepository;

    /**
     * Repository to interact with Course data source.
     */
    @Autowired
    private CourseRepository courseRepository;

    /**
     * Creates a new CourseContent.
     *
     * @param courseContentDTO the DTO containing course content data
     * @return the saved CourseContent
     * @throws ResourceAlreadyExistsException if   a course content with the same title already exists for the course
     * @throws ResourceNotFoundException if  the course does not exist
     */
    @Override
    public CourseContent createCourseContent(final CourseContentDTO courseContentDTO) {
        try {
            Optional<CourseContent> existingCourse = courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                    courseContentDTO.getTitle(), courseContentDTO.getCourseId());
            if (existingCourse.isPresent()) {
                throw new ResourceAlreadyExistsException(COURSE_CONTENT_ALREADY_PRESENT);
            }
            boolean courseExists = courseRepository.existsById(courseContentDTO.getCourseId());
            if  (!courseExists) {
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(courseContentDTO);
            courseContent.setCreatedAt(LocalDateTime.now());
            courseContent.setUpdatedAt(LocalDateTime.now());
            return courseContentRepository.save(courseContent);
        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Retrieves all course content records.
     *
     * @return list of CourseContent
     * @throws ResourceNotFoundException if  no course contents are found
     */
    @Override
    public List<CourseContent> getAllCourseContents() {
        try {
            List<CourseContent> courseContents = courseContentRepository.findAll();
            if  (courseContents.isEmpty()) {
                throw new ResourceNotFoundException(CONTENT_NOT_FOUND);
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Retrieves a course content by its ID.
     *
     * @param courseContentId the ID of the course content
     * @return Optional containing CourseContent if  found
     * @throws ResourceNotFoundException if  course content is not found
     */
    @Override
    public Optional<CourseContent> getCourseContentById(final Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if  (courseContent.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND);
            }
            return courseContent;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Deletes a course content by its ID.
     *
     * @param courseContentId the ID of the course content to delete
     * @return success message upon deletion
     * @throws ResourceNotFoundException of course content is not found
     */
    @Override
    public String deleteCourseContent(final Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if  (courseContent.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND);
            }
            courseContentRepository.delete(courseContent.get());
            return COURSE_CONTENT_DELETED;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Updates an existing course content by ID.
     *
     * @param courseId ID of the course content to update
     * @param updateCourseContentDTO the updated data
     * @return success message upon update
     * @throws ResourceNotFoundException of course content or course is not found
     * @throws ResourceAlreadyExistsException if  updated title and course combination already exists
     */
    @Override
    public String updateCourseContent(final Long courseId, final UpdateCourseContentDTO updateCourseContentDTO) {
        try {
            Optional<CourseContent> courseContentOptional = courseContentRepository.findById(courseId);
            if  (courseContentOptional.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND);
            }
            boolean courseExists = courseRepository.existsById(updateCourseContentDTO.getCourseId());
            if  (!courseExists) {
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            CourseContent existingCourseContent = courseContentOptional.get();

            boolean isTitleChanged = !existingCourseContent.getTitle().equalsIgnoreCase(updateCourseContentDTO.getTitle());
            boolean isCourseIdChanged = !(existingCourseContent.getCourseId() == updateCourseContentDTO.getCourseId());

            if  (isTitleChanged || isCourseIdChanged) {
                Optional<CourseContent> duplicate = courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                        updateCourseContentDTO.getTitle(), updateCourseContentDTO.getCourseId());

                if  (duplicate.isPresent() && !(duplicate.get().getCourseId() == courseId)) {
                    throw new ResourceAlreadyExistsException(COURSE_CONTENT_DUPLICATE);
                }
            }

            existingCourseContent.setCourseId(updateCourseContentDTO.getCourseId());
            existingCourseContent.setTitle(updateCourseContentDTO.getTitle());
            existingCourseContent.setDescription(updateCourseContentDTO.getDescription());
            existingCourseContent.setResourceLink(updateCourseContentDTO.getResourceLink());
            existingCourseContent.setUpdatedAt(LocalDateTime.now());
            existingCourseContent.setActive(updateCourseContentDTO.isActive());

            courseContentRepository.save(existingCourseContent);
            return COURSE_CONTENT_UPDATED;

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    /**
     * Retrieves all course content associated with a specif ic course.
     *
     * @param courseId the ID of the course
     * @return list of course contents for the course
     * @throws ResourceNotFoundException of course or its content is not found
     */
    @Override
    public List<CourseContent> getAllCourseContentByCourseId(final Long courseId) {
        try {
            boolean courseExists = courseRepository.existsById(courseId);
            if  (!courseExists) {
                throw new ResourceNotFoundException(CONTENT_NOT_FOUND);
            }
            List<CourseContent> courseContents = courseContentRepository.findByCourseId(courseId);
            if (courseContents.isEmpty()) {
                throw new ResourceNotFoundException(NO_COURSE_CONTENTS_FOUND);
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

}
