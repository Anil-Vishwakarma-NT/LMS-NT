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
            CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(courseContentDTO);
            return courseContentRepository.save(courseContent);
        } catch (ResourceAlreadyExistsException e) {
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
        return Optional.empty();
    }

    @Override
    public String deleteCourseContent(Long courseContentId) {
        return "";
    }

    @Override
    public String updateCourseContent(Long courseId, CourseContentDTO courseContentDTO) {
        return "";
    }
}
