package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.inDTO.CourseBundleInDTO;
import com.example.course_service_lms.inDTO.UpdateCourseBundleInDTO;
import com.example.course_service_lms.outDTO.BundleInfoOutDTO;
import com.example.course_service_lms.outDTO.BundleSummaryOutDTO;
import com.example.course_service_lms.outDTO.CourseBundleOutDTO;
import com.example.course_service_lms.repository.CourseBundleRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.service.CourseBundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.course_service_lms.constants.CourseBundleConstants.BUNDLE_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_BUNDLE_ALREADY_EXISTS;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_BUNDLE_NOT_FOUND_BY_ID;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_NOT_FOUND;
import static com.example.course_service_lms.constants.CourseBundleConstants.FAILED_TO_CREATE_COURSE_BUNDLE;
import static com.example.course_service_lms.constants.CourseBundleConstants.FAILED_TO_DELETE_COURSE_BUNDLE;
import static com.example.course_service_lms.constants.CourseBundleConstants.FAILED_TO_FETCH_COURSE_BUNDLE;
import static com.example.course_service_lms.constants.CourseBundleConstants.FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID;
import static com.example.course_service_lms.constants.CourseBundleConstants.FAILED_TO_UPDATE_COURSE_BUNDLE;
import static com.example.course_service_lms.constants.CourseBundleConstants.INVALID_BUNDLE_ID;
import static com.example.course_service_lms.constants.CourseBundleConstants.INVALID_COURSE_ID;
import static com.example.course_service_lms.constants.CourseBundleConstants.NO_COURSE_BUNDLES_FOUND;
import static com.example.course_service_lms.converters.CourseBundleConvertor.convertDTOToEntityPost;
import static com.example.course_service_lms.converters.CourseBundleConvertor.convertEntityToDTO;

/**
 * Implementation of the {@link CourseBundleService} interface that handles operations related to course-bundle mappings.
 * Provides functionality for creating, updating, deleting, and retrieving course-bundle records.
 * Validates inputs and handles exceptions appropriately.
 *
 * @see CourseBundleService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseBundleServiceImpl implements CourseBundleService {

    /**
     * Repository for performing CRUD operations on {@link CourseBundle} entity.
     */
    @Autowired
    private final CourseBundleRepository courseBundleRepository;

    @Autowired
    private final BundleRepository bundleRepository;
    /**
     * Repository for performing CRUD operations on {@link Course} entity.
     */
    @Autowired
    private final CourseRepository courseRepository;

    /**
     * Repository for performing CRUD operations on {@link Bundle} entity.
     */

    /**
     * Retrieves all course-bundle mappings from the repository.
     *
     * @return a list of {@link CourseBundleOutDTO} objects representing all course-bundle mappings
     * @throws ResourceNotFoundException if no course-bundle records are found
     * @throws RuntimeException if an unexpected error occurs during the process
     */
    @Override
    public List<CourseBundleOutDTO> getAllCourseBundles() {
        log.info("Fetching all course-bundle records");
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findAll();

            // Check if no records are found
            if (courseBundles.isEmpty()) {
                log.warn("No course-bundle records found");
                throw new ResourceNotFoundException(NO_COURSE_BUNDLES_FOUND);
            }

            // Convert entities to DTOs using the helper method
            // noinspection unused
            List<CourseBundleOutDTO> courseBundleOutDTOS = new ArrayList<>();
            for (CourseBundle courseBundle : courseBundles) {
                // noinspection unused
                CourseBundleOutDTO courseBundleOutDTO = convertEntityToDTO(courseBundle);
                Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
                if (bundle.isEmpty()) {
                    throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
                }
                String bundleName = bundle.get().getBundleName();
                courseBundleOutDTO.setBundleName(bundleName);


                Optional<Course> course  = courseRepository.findById(courseBundle.getBundleId());
                if (course.isEmpty()) {
                    throw new ResourceNotFoundException(COURSE_NOT_FOUND);
                }
                String courseName = course.get().getTitle();
                courseBundleOutDTO.setCourseName(courseName);

                courseBundleOutDTOS.add(courseBundleOutDTO);
            }

            log.info("Successfully retrieved {} course-bundle records", courseBundleOutDTOS.size());
            return courseBundleOutDTOS;

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching all records: {}", ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE, ex);
        }
    }

    /**
     * Retrieves a specific course-bundle mapping by its ID.
     *
     * @param courseBundleId the ID of the course-bundle mapping
     * @return the {@link CourseBundleOutDTO} object representing the course-bundle mapping
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     * @throws RuntimeException if an unexpected error occurs during the process
     */
    @Override
    public CourseBundleOutDTO getCourseBundleById(final Long courseBundleId) {
        log.info("Fetching course-bundle record with ID: {}", courseBundleId);
        try {
            // noinspection unused
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Convert entity to DTO
            CourseBundleOutDTO courseBundleOutDTO = convertEntityToDTO(courseBundle);

            Optional<Bundle> bundle = bundleRepository.findById(courseBundle.getBundleId());
            if (bundle.isEmpty()) {
                throw new ResourceNotFoundException(BUNDLE_NOT_FOUND);
            }
            String bundleName = bundle.get().getBundleName();
            courseBundleOutDTO.setBundleName(bundleName);


            Optional<Course> course  = courseRepository.findById(courseBundle.getBundleId());
            if (course.isEmpty()) {
                throw new ResourceNotFoundException(COURSE_NOT_FOUND);
            }
            String courseName = course.get().getTitle();
            courseBundleOutDTO.setCourseName(courseName);

            log.info("Successfully retrieved course-bundle record: {}", courseBundleOutDTO);
            return courseBundleOutDTO;

        }  catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception ex) {
            log.error("Unexpected error occurred while fetching record with ID {}: {}", courseBundleId, ex.getMessage());
            throw new RuntimeException(FAILED_TO_FETCH_COURSE_BUNDLE_BY_ID + courseBundleId, ex);
        }
    }

    /**
     * Deletes a course-bundle mapping by its ID.
     *
     * @param courseBundleId the ID of the course-bundle mapping to delete
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     * @throws RuntimeException if an unexpected error occurs during the process
     */
    @Override
    public void deleteCourseBundle(final Long courseBundleId) {
        try {
            log.info("Starting process to delete course-bundle record with ID: {}", courseBundleId);
            // noinspection unused
            CourseBundle courseBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            courseBundleRepository.delete(courseBundle);
            log.info("Successfully deleted course-bundle record with ID: {}", courseBundleId);

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_DELETE_COURSE_BUNDLE + courseBundleId, e);
        }
    }

    /**
     * Updates an existing course-bundle mapping.
     *
     * @param courseBundleId the ID of the course-bundle mapping to update
     * @param updateCourseBundleInDTO the updated data for the course-bundle mapping
     * @return the updated {@link CourseBundleInDTO} object
     * @throws ResourceNotFoundException if the course-bundle mapping with the given ID is not found
     * @throws RuntimeException if an unexpected error occurs during the update
     */
    @Override
    public String updateCourseBundle(final Long courseBundleId, final UpdateCourseBundleInDTO updateCourseBundleInDTO) {
        try {
            log.info("Starting process to update course-bundle record with ID: {}", courseBundleId);
            // noinspection unused
            CourseBundle existingBundle = courseBundleRepository.findById(courseBundleId)
                    .orElseThrow(() -> {
                        log.error("Course-bundle record not found for ID: {}", courseBundleId);
                        return new ResourceNotFoundException(COURSE_BUNDLE_NOT_FOUND_BY_ID + courseBundleId);
                    });

            // Update entity fields using the DTO
            existingBundle.setBundleId(updateCourseBundleInDTO.getBundleId());
            existingBundle.setCourseId(updateCourseBundleInDTO.getCourseId());
            existingBundle.setUpdatedAt(LocalDateTime.now());
            existingBundle.setActive(updateCourseBundleInDTO.isActive());

            // Save updated entity to the database
            CourseBundle updatedBundle = courseBundleRepository.save(existingBundle);
            log.info("Successfully updated course-bundle record with ID: {}", updatedBundle.getCourseBundleId());

            // Convert updated entity to DTO
            return "Course Bundle Updated Successfully";

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while updating record with ID {}: {}", courseBundleId, e.getMessage());
            throw new RuntimeException(FAILED_TO_UPDATE_COURSE_BUNDLE + courseBundleId, e);
        }
    }

    /**
     * Creates a new course-bundle mapping.
     *
     * @param courseBundleInDTO the data for the new course-bundle mapping
     * @return the created {@link CourseBundleInDTO} object
     * @throws ResourceAlreadyExistsException if the course-bundle mapping already exists
     * @throws ResourceNotValidException if the provided bundle ID or course ID is invalid
     * @throws RuntimeException if an unexpected error occurs during the creation process
     */
    @Override
    public CourseBundle createCourseBundle(final CourseBundleInDTO courseBundleInDTO) {
        try {
            log.info("Starting process to create a new course-bundle mapping");

            // Check if the resource already exists
            if (courseBundleRepository.existsByBundleIdAndCourseId(courseBundleInDTO.getBundleId(),
                    courseBundleInDTO.getCourseId())) {
                log.error("Course-bundle mapping already exists for Bundle ID: {} and Course ID: {}",
                        courseBundleInDTO.getBundleId(), courseBundleInDTO.getCourseId());
                throw new ResourceAlreadyExistsException(COURSE_BUNDLE_ALREADY_EXISTS);
            }

            // Validate Bundle ID
            if (!bundleRepository.existsById(courseBundleInDTO.getBundleId())) {
                log.error("Invalid Bundle ID: {}", courseBundleInDTO.getBundleId());
                throw new ResourceNotValidException(INVALID_BUNDLE_ID + courseBundleInDTO.getBundleId());
            }

            // Validate Course ID
            if (!courseRepository.existsById(courseBundleInDTO.getCourseId())) {
                log.error("Invalid Course ID: {}", courseBundleInDTO.getCourseId());
                throw new ResourceNotValidException(INVALID_COURSE_ID + courseBundleInDTO.getCourseId());
            }

            // Convert DTO to Entity
            CourseBundle courseBundle = convertDTOToEntityPost(courseBundleInDTO);
            courseBundle.setCreatedAt(LocalDateTime.now());
            courseBundle.setUpdatedAt(LocalDateTime.now());
            // Save entity to the database
            CourseBundle savedBundle = courseBundleRepository.save(courseBundle);
            log.info("Successfully created a new course-bundle mapping with ID: {}", savedBundle.getCourseBundleId());

            // Convert saved entity to DTO
            return savedBundle;

        } catch (ResourceAlreadyExistsException | ResourceNotValidException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException(FAILED_TO_CREATE_COURSE_BUNDLE, e);
        }
    }

    @Override
    public List<BundleInfoOutDTO> getBundlesInfo() {
        try {
            List<Bundle> courseBundles = bundleRepository.findAll();
            if(courseBundles.isEmpty()) {
                throw new ResourceNotFoundException("No courses added in bundle");
            }
            List<BundleInfoOutDTO> bundleInfoOutDTOS = new ArrayList<>();
            for(Bundle courseBundle : courseBundles) {
                BundleInfoOutDTO bundleInfoOutDTO = new BundleInfoOutDTO();
                Bundle bundle = bundleRepository.findById(courseBundle.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bundle not found"));
                Long countCoursesInBundle = courseBundleRepository.countByBundleId(bundle.getBundleId());
                bundleInfoOutDTO.setBundleId(courseBundle.getBundleId());
                bundleInfoOutDTO.setBundleName(bundle.getBundleName());
                bundleInfoOutDTO.setTotalCourses(countCoursesInBundle);
                bundleInfoOutDTO.setActive(bundle.isActive());
                bundleInfoOutDTO.setCreatedAt(bundle.getCreatedAt());
                bundleInfoOutDTO.setUpdatedAt(bundle.getUpdatedAt());
                bundleInfoOutDTOS.add(bundleInfoOutDTO);
            }
            return bundleInfoOutDTOS;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    /**
     * Retrieves all courses belonging to a specific bundle.
     *
     * @param bundleId the ID of the bundle
     * @return a list of {@link CourseBundle} objects for the specified bundle
     * @throws ResourceNotFoundException if no courses are found in the given bundle
     * @throws RuntimeException if an unexpected error occurs during the process
     */
    @Override
    public List<CourseBundle> getAllCoursesByBundle(final Long bundleId) {
        try {
            List<CourseBundle> courseBundles = courseBundleRepository.findByBundleId(bundleId);
            if (courseBundles.isEmpty()) {
                throw new ResourceNotFoundException("No courses in the bundle");
            }
            return courseBundles;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating course-bundle mapping: {}", e.getMessage());
            throw new RuntimeException("Something went wrong");
        }
    }

    @Override
    public List<BundleSummaryOutDTO> getRecentBundleSummaries() {
        // Get the 5 most recent bundles
        List<Bundle> recentBundles = bundleRepository.findTop5ByOrderByCreatedAtDesc();

        // Create the DTOs with course counts
        return recentBundles.stream()
                .map(bundle -> {
                    long courseCount = courseBundleRepository.countByBundleId(bundle.getBundleId());
                    return new BundleSummaryOutDTO(
                            bundle.getBundleId(),
                            bundle.getBundleName(),
                            courseCount,
                            bundle.getCreatedAt(),
                            bundle.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }


}
