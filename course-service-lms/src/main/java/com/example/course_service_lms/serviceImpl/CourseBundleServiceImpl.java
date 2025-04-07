package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.CourseBundleRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseBundleServiceImpl implements CourseBundleService {

    @Autowired
    private final CourseBundleRepository courseBundleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @Override
    public List<CourseBundleDTO> getAllCourseBundles() {
        log.info("Fetching all course-bundle records");
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findAll();

            // Check if no records are found
            if (courseBundles.isEmpty()) {
                log.warn("No course-bundle records found");
                throw new ResourceNotFoundException("No course-bundle records found");
            }

            // Convert entities to DTOs using the helper method
            List<CourseBundleDTO> courseBundleDTOs = new ArrayList<>();
            for (CourseBundle courseBundle : courseBundles) {
                courseBundleDTOs.add(convertEntityToDTO(courseBundle));
            }

            log.info("Successfully retrieved {} course-bundle records", courseBundleDTOs.size());
            return courseBundleDTOs;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching all records: {}", ex.getMessage());
            throw new RuntimeException("Failed to fetch course-bundle records", ex);
        }
    }

    @Override
    public CourseBundleDTO getCourseBundleById(Long courseBundleId) {
        log.info("Fetching course-bundle record with ID: {}", courseBundleId);
        try {
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException("Course-bundle record not found for ID: " + courseBundleId);
                    });

            // Convert entity to DTO
            CourseBundleDTO courseBundleDTO = convertEntityToDTO(courseBundle);
            log.info("Successfully retrieved course-bundle record: {}", courseBundleDTO);
            return courseBundleDTO;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching record with ID {}: {}", courseBundleId, ex.getMessage());
            throw new RuntimeException("Failed to fetch course-bundle record with ID: " + courseBundleId, ex);
        }
    }

    @Override
    public void deleteCourseBundle(Long courseBundleId) {
        try {
            log.info("Starting process to delete course-bundle record with ID: {}", courseBundleId);

            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException("Course-bundle record not found for ID: " + courseBundleId);
                    });

            courseBundleRepository.delete(courseBundle);
            log.info("Successfully deleted course-bundle record with ID: {}", courseBundleId);

        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException("Something went wrong while deleting course-bundle record with ID: " + courseBundleId, e);
        }
    }

    @Override
    public CourseBundleDTO updateCourseBundle(Long courseBundleId, CourseBundleDTO courseBundleDTO) {
        try {
            log.info("Starting process to update course-bundle record with ID: {}", courseBundleId);

            CourseBundle existingBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException("Course-bundle record not found for ID: " + courseBundleId);
                    });

            // Update entity fields using the DTO
            existingBundle.setBundleId(courseBundleDTO.getBundleId());
            existingBundle.setCourseId(courseBundleDTO.getCourseId());

            // Save updated entity to the database
            CourseBundle updatedBundle = courseBundleRepository.save(existingBundle);
            log.info("Successfully updated course-bundle record with ID: {}", updatedBundle.getCourseBundleId());

            // Convert updated entity to DTO
            return convertEntityToDTO(updatedBundle);

        } catch (Exception e) {
            log.error("Unexpected error occurred while updating record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException("Something went wrong while updating course-bundle record with ID: " + courseBundleId, e);
        }
    }

    @Override
    public CourseBundleDTO createCourseBundle(CourseBundleDTO courseBundleDTO) {
        try {
            log.info("Starting process to create a new course-bundle mapping");

            // Check if the resource already exists
            if (courseBundleRepository.existsByBundleIdAndCourseId(courseBundleDTO.getBundleId(), courseBundleDTO.getCourseId())) {
                log.error("Course-bundle mapping already exists for Bundle ID: {} and Course ID: {}",
                        courseBundleDTO.getBundleId(), courseBundleDTO.getCourseId());
                throw new ResourceAlreadyExistsException("Course-bundle mapping already exists for the provided Bundle ID and Course ID");
            }

            // Validate Bundle ID
            if (!bundleRepository.existsById(courseBundleDTO.getBundleId())) {
                log.error("Invalid Bundle ID: {}", courseBundleDTO.getBundleId());
                throw new ResourceNotValidException("Invalid Bundle ID: " + courseBundleDTO.getBundleId());
            }

            // Validate Course ID
            if (!courseRepository.existsById(courseBundleDTO.getCourseId())) {
                log.error("Invalid Course ID: {}", courseBundleDTO.getCourseId());
                throw new ResourceNotValidException("Invalid Course ID: " + courseBundleDTO.getCourseId());
            }

            // Convert DTO to Entity
            CourseBundle courseBundle = convertDTOToEntity(courseBundleDTO);

            // Save entity to the database
            CourseBundle savedBundle = courseBundleRepository.save(courseBundle);
            log.info("Successfully created a new course-bundle mapping with ID: {}", savedBundle.getCourseBundleId());

            // Convert saved entity to DTO
            return convertEntityToDTO(savedBundle);

        } catch(ResourceNotValidException e){
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException("Something went wrong while creating course-bundle mapping", e);
        }
    }

    // Helper Method: DTO to Entity
    private CourseBundle convertDTOToEntity(CourseBundleDTO courseBundleDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleDTO.getBundleId());
        courseBundle.setCourseId(courseBundleDTO.getCourseId());
        return courseBundle;
    }

    // Helper Method: Entity to DTO
    private CourseBundleDTO convertEntityToDTO(CourseBundle courseBundle) {
        CourseBundleDTO courseBundleDTO = new CourseBundleDTO();
        courseBundleDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundleDTO.setBundleId(courseBundle.getBundleId());
        courseBundleDTO.setCourseId(courseBundle.getCourseId());

        String bundleName = bundleRepository.findById(courseBundle.getBundleId())
                .get()
                .getBundleName();
        courseBundleDTO.setBundleName(bundleName);

        String courseName = courseRepository.findById(courseBundle.getCourseId())
                .get()
                .getTitle();
        courseBundleDTO.setCourseName(courseName);

        return courseBundleDTO;
    }
}