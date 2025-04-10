package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.entity.Course;
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
import java.util.Optional;

import static com.example.course_service_lms.constants.CourseBundleConstants.*;
import static com.example.course_service_lms.converters.CourseBundleConvertor.convertDTOToEntity;
import static com.example.course_service_lms.converters.CourseBundleConvertor.convertEntityToDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseBundleServiceImpl implements CourseBundleService {

    @Autowired
    private final CourseBundleRepository courseBundleRepository;

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final BundleRepository bundleRepository;

    @Override
    public List<CourseBundleDTO> getAllCourseBundles() {
        log.info("Fetching all course-bundle records");
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findAll();

            // Check if no records are found
            if (courseBundles.isEmpty()) {
                log.warn("No course-bundle records found");
                throw new ResourceNotFoundException(NO_COURSE_BUNDLES_FOUND);
            }

            // Convert entities to DTOs using the helper method
            List<CourseBundleDTO> courseBundleDTOs = new ArrayList<>();
            for (CourseBundle courseBundle : courseBundles) {
                CourseBundleDTO courseBundleDTO = convertEntityToDTO(courseBundle);
                Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
                if(bundle.isEmpty()){
                    throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
                }
                String bundleName = bundle.get().getBundleName();
                courseBundleDTO.setBundleName(bundleName);


                Optional<Course> course  = courseRepository.findById(courseBundle.getBundleId());
                if(course.isEmpty()){
                    throw new ResourceNotFoundException(COURSE_NOT_FOUND);
                }
                String courseName = course.get().getTitle();
                courseBundleDTO.setCourseName(courseName);

                courseBundleDTOs.add(courseBundleDTO);
            }

            log.info("Successfully retrieved {} course-bundle records", courseBundleDTOs.size());
            return courseBundleDTOs;

        } catch(ResourceNotFoundException e){
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching all records: {}", ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE, ex);
        }
    }

    @Override
    public CourseBundleDTO getCourseBundleById(Long courseBundleId) {
        log.info("Fetching course-bundle record with ID: {}", courseBundleId);
        try {
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Convert entity to DTO
            CourseBundleDTO courseBundleDTO = convertEntityToDTO(courseBundle);

            Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
            if(bundle.isEmpty()){
                throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
            }
            String bundleName = bundle.get().getBundleName();
            courseBundleDTO.setBundleName(bundleName);


            Optional<Course> course  = courseRepository.findById(courseBundle.getBundleId());
            if(course.isEmpty()){
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            String courseName = course.get().getTitle();
            courseBundleDTO.setCourseName(courseName);

            log.info("Successfully retrieved course-bundle record: {}", courseBundleDTO);
            return courseBundleDTO;

        }  catch(ResourceNotFoundException e){
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching record with ID {}: {}", courseBundleId, ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID + courseBundleId, ex);
        }
    }

    @Override
    public void deleteCourseBundle(Long courseBundleId) {
        try {
            log.info("Starting process to delete course-bundle record with ID: {}", courseBundleId);

            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            courseBundleRepository.delete(courseBundle);
            log.info("Successfully deleted course-bundle record with ID: {}", courseBundleId);

        }  catch(ResourceNotFoundException e){
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_DELETE_COURSE_BUNDLE + courseBundleId, e);
        }
    }

    @Override
    public CourseBundleDTO updateCourseBundle(Long courseBundleId, CourseBundleDTO courseBundleDTO) {
        try {
            log.info("Starting process to update course-bundle record with ID: {}", courseBundleId);

            CourseBundle existingBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Update entity fields using the DTO
            existingBundle.setBundleId(courseBundleDTO.getBundleId());
            existingBundle.setCourseId(courseBundleDTO.getCourseId());

            // Save updated entity to the database
            CourseBundle updatedBundle = courseBundleRepository.save(existingBundle);
            log.info("Successfully updated course-bundle record with ID: {}", updatedBundle.getCourseBundleId());

            // Convert updated entity to DTO
            return convertEntityToDTO(updatedBundle);

        }  catch(ResourceNotFoundException e){
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while updating record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_UPDATE_COURSE_BUNDLE + courseBundleId, e);
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
                throw new ResourceAlreadyExistsException(COURSE_BUNDLE_ALREADY_EXISTS);
            }

            // Validate Bundle ID
            if (!bundleRepository.existsById(courseBundleDTO.getBundleId())) {
                log.error("Invalid Bundle ID: {}", courseBundleDTO.getBundleId());
                throw new ResourceNotValidException(INVALID_BUNDLE_ID + courseBundleDTO.getBundleId());
            }

            // Validate Course ID
            if (!courseRepository.existsById(courseBundleDTO.getCourseId())) {
                log.error("Invalid Course ID: {}", courseBundleDTO.getCourseId());
                throw new ResourceNotValidException(INVALID_COURSE_ID + courseBundleDTO.getCourseId());
            }

            // Convert DTO to Entity
            CourseBundle courseBundle = convertDTOToEntity(courseBundleDTO);

            // Save entity to the database
            CourseBundle savedBundle = courseBundleRepository.save(courseBundle);
            log.info("Successfully created a new course-bundle mapping with ID: {}", savedBundle.getCourseBundleId());

            // Convert saved entity to DTO
            return convertEntityToDTO(savedBundle);

        }  catch(ResourceAlreadyExistsException | ResourceNotValidException e){
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException(FAILED_TO_CREATE_COURSE_BUNDLE, e);
        }
    }

}