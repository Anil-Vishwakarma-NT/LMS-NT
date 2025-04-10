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
                throw new ResourceAlreadyExistsException("Course Content Already Present");
            }
            boolean courseExists = courseRepository.existsById(courseContentDTO.getCourseId());
            if (!courseExists){
               throw new ResourceNotFoundException("Course does not exists");
            }
            CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(courseContentDTO);
            return courseContentRepository.save(courseContent);
        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public List<CourseContent> getAllCourseContents() {
        try {
            List<CourseContent> courseContents = courseContentRepository.findAll();
            if (courseContents.isEmpty()) {
                throw new ResourceNotFoundException("No Contents Found");
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public Optional<CourseContent> getCourseContentById(Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if (courseContent.isEmpty()) {
                throw new ResourceNotFoundException("Course Content Not Found");
            }
           return courseContent;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public String deleteCourseContent(Long courseContentId) {
        try {
            Optional<CourseContent> courseContent = courseContentRepository.findById(courseContentId);
            if (courseContent.isEmpty()) {
                throw new ResourceNotFoundException("Course Content Not Found");
            }
            courseContentRepository.delete(courseContent.get());
            return "Course Content Deleted Successfully";
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public String updateCourseContent(Long courseId, CourseContentDTO courseContentDTO) {
        try {
            Optional<CourseContent> courseContentOptional = courseContentRepository.findById(courseId);
            if (courseContentOptional.isEmpty()) {
                throw new ResourceNotFoundException("Course Content Not Found");
            }
            boolean courseExists = courseRepository.existsById(courseContentDTO.getCourseId());
            if (!courseExists){
                throw new ResourceNotFoundException("Course does not exists");
            }
            CourseContent existingCourseContent = courseContentOptional.get();

            boolean isTitleChanged = !existingCourseContent.getTitle().equalsIgnoreCase(courseContentDTO.getTitle());
            boolean isCourseIdChanged = !(existingCourseContent.getCourseId() == courseContentDTO.getCourseId());

            if (isTitleChanged || isCourseIdChanged) {
                Optional<CourseContent> duplicate = courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                        courseContentDTO.getTitle(), courseContentDTO.getCourseId());

                if (duplicate.isPresent() && !(duplicate.get().getCourseId() == courseId)) {
                    throw new ResourceAlreadyExistsException("Course content with the same title already exists for this course.");
                }
            }

            existingCourseContent.setCourseId(courseContentDTO.getCourseId());
            existingCourseContent.setTitle(courseContentDTO.getTitle());
            existingCourseContent.setDescription(courseContentDTO.getDescription());
            existingCourseContent.setVideoLink(courseContentDTO.getVideoLink());
            existingCourseContent.setResourceLink(courseContentDTO.getResourceLink());

            courseContentRepository.save(existingCourseContent);
            return "Course content updated successfully";

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public List<CourseContent> getAllCourseContentByCourseId(Long courseId) {
        try{
            boolean courseExists = courseRepository.existsById(courseId);
            if (!courseExists){
                throw new ResourceNotFoundException("Course does not exists");
            }
            List<CourseContent> courseContents = courseContentRepository.findByCourseId(courseId);
            if(courseContents.isEmpty()) {
                throw new ResourceNotFoundException("No Course Contents Found");
            }
            return courseContents;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }

}
