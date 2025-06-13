package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.converters.CourseContentConverters;
import com.example.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.example.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.CourseContentConstants.*;

/**
 * Optimized implementation of the {@link CourseContentService} interface that handles operations related to course content.
 * Provides functionality for creating, updating, deleting, and retrieving course content records.
 * Validates inputs and handles exceptions appropriately.
 *
 * @see CourseContentService
 */
@Slf4j
@Service
@Transactional
public class CourseContentImpl implements CourseContentService {

    private final CourseContentRepository courseContentRepository;
    private final CourseRepository courseRepository;

    /**
     * Constructor-based dependency injection for better testability and immutability.
     *
     * @param courseContentRepository repository for course content operations
     * @param courseRepository repository for course operations
     */
    @Autowired
    public CourseContentImpl(CourseContentRepository courseContentRepository,
                             CourseRepository courseRepository) {
        this.courseContentRepository = courseContentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Creates a new CourseContent and returns the created content as DTO.
     *
     * @param courseContentInDTO the DTO containing course content data
     * @return the created CourseContentOutDTO
     * @throws ResourceAlreadyExistsException if a course content with the same title already exists for the course
     * @throws ResourceNotFoundException if the course does not exist
     */
    @Override
    public CourseContentOutDTO createCourseContent(final CourseContentInDTO courseContentInDTO) {
        try {
            log.debug("Creating course content with title: {} for course ID: {}",
                    courseContentInDTO.getTitle(), courseContentInDTO.getCourseId());

            validateCourseContentCreation(courseContentInDTO);

            CourseContent courseContent = CourseContentConverters.courseContentInDtoToEntity(courseContentInDTO);
            setAuditFields(courseContent);

            CourseContent savedContent = courseContentRepository.save(courseContent);
            log.info("Successfully created course content with ID: {}", savedContent.getCourseContentId());

            return CourseContentConverters.entityToOutDto(savedContent);

        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            log.error("Validation error while creating course content: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating course content", e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all course content records as DTOs.
     *
     * @return list of CourseContentOutDTO
     * @throws ResourceNotFoundException if no course contents are found
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseContentOutDTO> getAllCourseContents() {
        try {
            log.debug("Retrieving all course contents");

            List<CourseContent> courseContents = courseContentRepository.findAll();
            if (courseContents.isEmpty()) {
                log.warn("No course contents found");
                throw new ResourceNotFoundException(CONTENT_NOT_FOUND);
            }

            log.info("Found {} course contents", courseContents.size());
            return CourseContentConverters.entityListToOutDtoList(courseContents);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving all course contents", e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves a course content by its ID as DTO.
     *
     * @param courseContentId the ID of the course content
     * @return CourseContentOutDTO if found
     * @throws ResourceNotFoundException if course content is not found
     */
    @Override
    @Transactional(readOnly = true)
    public CourseContentOutDTO getCourseContentById(final Long courseContentId) {
        try {
            log.debug("Retrieving course content with ID: {}", courseContentId);

            CourseContent courseContent = courseContentRepository.findById(courseContentId)
                    .orElseThrow(() -> new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND));

            log.info("Found course content with ID: {}", courseContentId);
            return CourseContentConverters.entityToOutDto(courseContent);

        } catch (ResourceNotFoundException e) {
            log.warn("Course content not found with ID: {}", courseContentId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving course content with ID: {}", courseContentId, e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Deletes a course content by its ID.
     *
     * @param courseContentId the ID of the course content to delete
     * @return success message upon deletion
     * @throws ResourceNotFoundException if course content is not found
     */
    @Override
    public String deleteCourseContent(final Long courseContentId) {
        try {
            log.debug("Deleting course content with ID: {}", courseContentId);

            CourseContent courseContent = courseContentRepository.findById(courseContentId)
                    .orElseThrow(() -> new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND));

            courseContentRepository.delete(courseContent);
            log.info("Successfully deleted course content with ID: {}", courseContentId);

            return COURSE_CONTENT_DELETED;

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot delete - course content not found with ID: {}", courseContentId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting course content with ID: {}", courseContentId, e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Updates an existing course content by ID and returns the updated content as DTO.
     *
     * @param courseContentId ID of the course content to update
     * @param updateCourseContentInDTO the updated data
     * @return updated CourseContentOutDTO
     * @throws ResourceNotFoundException if course content or course is not found
     * @throws ResourceAlreadyExistsException if updated title and course combination already exists
     */
    @Override
    public CourseContentOutDTO updateCourseContent(final Long courseContentId,
                                                   final UpdateCourseContentInDTO updateCourseContentInDTO) {
        try {
            log.debug("Updating course content with ID: {}", courseContentId);

            CourseContent existingContent = courseContentRepository.findById(courseContentId)
                    .orElseThrow(() -> new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND));

            validateCourseContentUpdate(existingContent, updateCourseContentInDTO, courseContentId);

            CourseContentConverters.updateEntityFromDto(existingContent, updateCourseContentInDTO);
            existingContent.setUpdatedAt(LocalDateTime.now());

            CourseContent updatedContent = courseContentRepository.save(existingContent);
            log.info("Successfully updated course content with ID: {}", courseContentId);

            return CourseContentConverters.entityToOutDto(updatedContent);

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            log.error("Validation error while updating course content: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating course content with ID: {}", courseContentId, e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all course content associated with a specific course as DTOs.
     *
     * @param courseId the ID of the course
     * @return list of CourseContentOutDTO for the course
     * @throws ResourceNotFoundException if course or its content is not found
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseContentOutDTO> getAllCourseContentByCourseId(final Long courseId) {
        try {
            log.debug("Retrieving course contents for course ID: {}", courseId);

            if (!courseRepository.existsById(courseId)) {
                log.warn("Course not found with ID: {}", courseId);
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }

            List<CourseContent> courseContents = courseContentRepository.findByCourseId(courseId);
            if (courseContents.isEmpty()) {
                log.warn("No course contents found for course ID: {}", courseId);
                throw new ResourceNotFoundException(NO_COURSE_CONTENTS_FOUND);
            }

            log.info("Found {} course contents for course ID: {}", courseContents.size(), courseId);
            return CourseContentConverters.entityListToOutDtoList(courseContents);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving course contents for course ID: {}", courseId, e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Validates course content creation requirements.
     */
    private void validateCourseContentCreation(final CourseContentInDTO courseContentInDTO) {
        // Check if course content with same title already exists for the course
        Optional<CourseContent> existingContent = courseContentRepository
                .findByTitleIgnoreCaseAndCourseId(courseContentInDTO.getTitle(), courseContentInDTO.getCourseId());

        if (existingContent.isPresent()) {
            throw new ResourceAlreadyExistsException(COURSE_CONTENT_ALREADY_PRESENT);
        }

        // Check if course exists
        if (!courseRepository.existsById(courseContentInDTO.getCourseId())) {
            throw new ResourceNotFoundException(COURSE_NOT_FOUND);
        }
    }

    /**
     * Validates course content update requirements.
     */
    private void validateCourseContentUpdate(final CourseContent existingContent,
                                             final UpdateCourseContentInDTO updateDto,
                                             final Long courseContentId) {
        // Check if course exists
        if (!courseRepository.existsById(updateDto.getCourseId())) {
            throw new ResourceNotFoundException(COURSE_NOT_FOUND);
        }

        // Check for duplicate title-course combination only if title or course ID changed
        boolean isTitleChanged = !existingContent.getTitle().equalsIgnoreCase(updateDto.getTitle());
        boolean isCourseIdChanged = existingContent.getCourseId() != updateDto.getCourseId();

        if (isTitleChanged || isCourseIdChanged) {
            Optional<CourseContent> duplicate = courseContentRepository
                    .findByTitleIgnoreCaseAndCourseId(updateDto.getTitle(), updateDto.getCourseId());

            if (duplicate.isPresent() && duplicate.get().getCourseContentId() != courseContentId) {
                throw new ResourceAlreadyExistsException(COURSE_CONTENT_DUPLICATE);
            }
        }
    }

    /**
     * Sets audit fields for new course content.
     */
    private void setAuditFields(final CourseContent courseContent) {
        LocalDateTime now = LocalDateTime.now();
        courseContent.setCreatedAt(now);
        courseContent.setUpdatedAt(now);
    }
}