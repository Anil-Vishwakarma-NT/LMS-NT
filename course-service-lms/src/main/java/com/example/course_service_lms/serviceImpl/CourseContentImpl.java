package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.CourseContentConverters;
import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.course_service_lms.constants.BundleConstants.GENERAL_ERROR;
import static com.example.course_service_lms.constants.CourseContentConstants.*;

@Service
public class CourseContentImpl implements CourseContentService {

    @Autowired
    private CourseContentRepository courseContentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public CourseContent createCourseContent(CourseContentDTO courseContentDTO) {
        try {
            Optional<CourseContent> existingCourse = courseContentRepository.findByTitleIgnoreCaseAndCourseId(courseContentDTO.getTitle(),courseContentDTO.getCourseId());
            if(existingCourse.isPresent()) {
                throw new ResourceAlreadyExistsException(COURSE_CONTENT_ALREADY_PRESENT);
            }
            boolean courseExists = courseRepository.existsById(courseContentDTO.getCourseId());
            if (!courseExists){
               throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(courseContentDTO);
            return courseContentRepository.save(courseContent);
        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    @Override
    public List<CourseContent> getAllCourseContents() {
        try {
            List<CourseContent> courseContents = courseContentRepository.findAll();
            if (courseContents.isEmpty()) {
                throw new ResourceNotFoundException(CONTENT_NOT_FOUND);
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    @Override
    public Optional<CourseContent> getCourseContentById(Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if (courseContent.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND);
            }
           return courseContent;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    @Override
    public String deleteCourseContent(Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if (courseContent.isEmpty()) {
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

    @Override
    public String updateCourseContent(Long courseId, CourseContentDTO courseContentDTO) {
        try {
            Optional<CourseContent> courseContentOptional = courseContentRepository.findById(courseId);
            if (courseContentOptional.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_CONTENT_NOT_FOUND);
            }
            boolean courseExists = courseRepository.existsById(courseContentDTO.getCourseId());
            if (!courseExists){
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            CourseContent existingCourseContent = courseContentOptional.get();

            boolean isTitleChanged = !existingCourseContent.getTitle().equalsIgnoreCase(courseContentDTO.getTitle());
            boolean isCourseIdChanged = !(existingCourseContent.getCourseId() == courseContentDTO.getCourseId());

            if (isTitleChanged || isCourseIdChanged) {
                Optional<CourseContent> duplicate = courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                        courseContentDTO.getTitle(), courseContentDTO.getCourseId());

                if (duplicate.isPresent() && !(duplicate.get().getCourseId() == courseId)) {
                    throw new ResourceAlreadyExistsException(COURSE_CONTENT_DUPLICATE);
                }
            }

            existingCourseContent.setCourseId(courseContentDTO.getCourseId());
            existingCourseContent.setTitle(courseContentDTO.getTitle());
            existingCourseContent.setDescription(courseContentDTO.getDescription());
            existingCourseContent.setVideoLink(courseContentDTO.getVideoLink());
            existingCourseContent.setResourceLink(courseContentDTO.getResourceLink());

            courseContentRepository.save(existingCourseContent);
            return COURSE_CONTENT_UPDATED;

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    @Override
    public List<CourseContent> getAllCourseContentByCourseId(Long courseId) {
        try{
            boolean courseExists = courseRepository.existsById(courseId);
            if (!courseExists){
                throw new ResourceNotFoundException(CONTENT_NOT_FOUND);
            }
            List<CourseContent> courseContents = courseContentRepository.findByCourseId(courseId);
            if(courseContents.isEmpty()) {
                throw new ResourceNotFoundException(NO_COURSE_CONTENTS_FOUND);
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

}
