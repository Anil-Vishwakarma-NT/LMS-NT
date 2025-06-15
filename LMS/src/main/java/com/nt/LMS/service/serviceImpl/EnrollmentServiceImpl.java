package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.dto.inDTO.EnrollmentRequestDTO;
import com.nt.LMS.entities.*;
import com.nt.LMS.exception.ResourceAlreadyExistsException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.ResourceNotValidException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.*;
import com.nt.LMS.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    @Autowired
    private UserGroupRepository userGroupRepository;

    private static final String ENROLLMENT_SOURCE_INDIVIDUAL = "INDIVIDUAL";
    private static final String ENROLLMENT_SOURCE_GROUP = "GROUP";
    private static final String ENROLLMENT_SOURCE_BUNDLE_EXPANSION = "BUNDLE_EXPANSION";

    @Override
    public List<Enrollment> enroll(EnrollmentRequestDTO requestDTO) {
        // Validate request
        validateEnrollmentRequest(requestDTO);

        List<Enrollment> createdEnrollments = new ArrayList<>();

        try {
            if (requestDTO.hasUsers()) {
                if (requestDTO.hasCourses()) {
                    // User to Course enrollment
                    createdEnrollments.addAll(enrollUsersToCourses(requestDTO));
                } else if (requestDTO.hasBundles()) {
                    // User to Bundle enrollment
                    createdEnrollments.addAll(enrollUsersToBundles(requestDTO));
                }
            } else if (requestDTO.hasGroups()) {
                if (requestDTO.hasCourses()) {
                    // Group to Course enrollment
                    createdEnrollments.addAll(enrollGroupsToCourses(requestDTO));
                } else if (requestDTO.hasBundles()) {
                    // Group to Bundle enrollment
                    createdEnrollments.addAll(enrollGroupsToBundles(requestDTO));
                }
            }

            return createdEnrollments;

        } catch (Exception e) {
            throw new ResourceNotValidException("Enrollment failed: " + e.getMessage());
        }
    }

    private void validateEnrollmentRequest(EnrollmentRequestDTO requestDTO) {
        if (!requestDTO.isValid()) {
            throw new ResourceNotValidException("Invalid enrollment request. Must provide either users or groups (not both) and either courses or bundles (not both).");
        }

        // Verify assigned by user exists
        if (!userRepository.existsById(requestDTO.getAssignedBy())) {
            throw new ResourceNotFoundException("Assigned by user not found with ID: " + requestDTO.getAssignedBy());
        }

        // Validate users exist
        if (requestDTO.hasUsers()) {
            List<Long> existingUserIds = userRepository.findExistingIds(requestDTO.getUserIds());
            if (existingUserIds.size() != requestDTO.getUserIds().size()) {
                List<Long> missing = new ArrayList<>(requestDTO.getUserIds());
                missing.removeAll(existingUserIds);
                throw new ResourceNotFoundException("Users not found with IDs: " + missing);
            }
        }

        // Validate groups exist
        if (requestDTO.hasGroups()) {
            List<Long> existingGroupIds = groupRepository.findExistingIds(requestDTO.getGroupIds());
            if (existingGroupIds.size() != requestDTO.getGroupIds().size()) {
                List<Long> missing = new ArrayList<>(requestDTO.getGroupIds());
                missing.removeAll(existingGroupIds);
                throw new ResourceNotFoundException("Groups not found with IDs: " + missing);
            }
        }

        // Validate courses exist
        if (requestDTO.hasCourses()) {
            List<Long> existingCourseIds = courseMicroserviceClient.getExistingCourseIds(requestDTO.getCourseIds()).getBody();
            if (existingCourseIds.size() != requestDTO.getCourseIds().size()) {
                List<Long> missing = new ArrayList<>(requestDTO.getCourseIds());
                missing.removeAll(existingCourseIds);
                throw new ResourceNotFoundException("Courses not found with IDs: " + missing);
            }
        }

        // Validate bundles exist
        if (requestDTO.hasBundles()) {
            List<Long> existingBundleIds = courseMicroserviceClient.getExistingBundleIds(requestDTO.getBundleIds()).getBody();
            if (existingBundleIds.size() != requestDTO.getBundleIds().size()) {
                List<Long> missing = new ArrayList<>(requestDTO.getBundleIds());
                missing.removeAll(existingBundleIds);
                throw new ResourceNotFoundException("Bundles not found with IDs: " + missing);
            }
        }
    }

    private List<Enrollment> enrollUsersToCourses(EnrollmentRequestDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long courseId : requestDTO.getCourseIds()) {
                Enrollment enrollment = handleUserCourseEnrollment(userId, courseId, requestDTO);
                if (enrollment != null) {
                    enrollments.add(enrollment);
                }
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollUsersToBundles(EnrollmentRequestDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long bundleId : requestDTO.getBundleIds()) {
                // Create the bundle enrollment
                Enrollment bundleEnrollment = handleUserBundleEnrollment(userId, bundleId, requestDTO);
                if (bundleEnrollment != null) {
                    enrollments.add(bundleEnrollment);

                    // Expand bundle to individual course enrollments
                    List<Enrollment> expandedEnrollments = expandBundleToIndividualCourses(
                            bundleEnrollment, userId, bundleId, requestDTO);
                    enrollments.addAll(expandedEnrollments);
                }
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollGroupsToCourses(EnrollmentRequestDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            for (Long courseId : requestDTO.getCourseIds()) {
                // Create group enrollment
                Enrollment groupEnrollment = handleGroupCourseEnrollment(groupId, courseId, requestDTO);
                if (groupEnrollment != null) {
                    enrollments.add(groupEnrollment);

                    // Create individual enrollments for group members if not group-only
                    if (!requestDTO.isGroupEnrollmentOnly()) {
                        List<Enrollment> memberEnrollments = createIndividualEnrollmentsForGroupMembers(
                                groupEnrollment, groupId, courseId, requestDTO);
                        enrollments.addAll(memberEnrollments);
                    }
                }
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollGroupsToBundles(EnrollmentRequestDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            for (Long bundleId : requestDTO.getBundleIds()) {
                // Create group bundle enrollment
                Enrollment groupBundleEnrollment = handleGroupBundleEnrollment(groupId, bundleId, requestDTO);
                if (groupBundleEnrollment != null) {
                    enrollments.add(groupBundleEnrollment);

                    if (!requestDTO.isGroupEnrollmentOnly()) {
                        // Get group members
                        List<Long> memberIds = userGroupRepository.findUserIdsByGroupId(groupId);

                        for (Long memberId : memberIds) {
                            // Create bundle enrollment for each member
                            Enrollment memberBundleEnrollment = createEnrollment(
                                    memberId, null, null, bundleId,
                                    ENROLLMENT_SOURCE_GROUP, groupBundleEnrollment.getEnrollmentId(),
                                    requestDTO);
                            enrollments.add(memberBundleEnrollment);

                            // Expand bundle to courses for each member
                            List<Enrollment> expandedEnrollments = expandBundleToIndividualCourses(
                                    memberBundleEnrollment, memberId, bundleId, requestDTO);
                            enrollments.addAll(expandedEnrollments);
                        }
                    }
                }
            }
        }

        return enrollments;
    }

    private Enrollment handleUserCourseEnrollment(Long userId, Long courseId, EnrollmentRequestDTO requestDTO) {
        // Check for existing enrollment
        Optional<Enrollment> existingEnrollment = enrollmentRepository
                .findActiveEnrollmentByUserAndCourse(userId, courseId);

        if (existingEnrollment.isPresent()) {
            if (!requestDTO.isForceEnrollment()) {
                throw new ResourceAlreadyExistsException(
                        "User " + userId + " is already enrolled in course " + courseId);
            } else {
                // Update existing enrollment
                Enrollment existing = existingEnrollment.get();
                updateEnrollmentDetails(existing, requestDTO);
                return enrollmentRepository.save(existing);
            }
        }

        return createEnrollment(userId, null, courseId, null,
                ENROLLMENT_SOURCE_INDIVIDUAL, null, requestDTO);
    }

    private Enrollment handleUserBundleEnrollment(Long userId, Long bundleId, EnrollmentRequestDTO requestDTO) {
        // Check for existing bundle enrollment
        Optional<Enrollment> existingEnrollment = enrollmentRepository
                .findActiveEnrollmentByUserAndBundle(userId, bundleId);

        if (existingEnrollment.isPresent()) {
            if (!requestDTO.isForceEnrollment()) {
                throw new ResourceAlreadyExistsException(
                        "User " + userId + " is already enrolled in bundle " + bundleId);
            } else {
                Enrollment existing = existingEnrollment.get();
                updateEnrollmentDetails(existing, requestDTO);
                return enrollmentRepository.save(existing);
            }
        }

        return createEnrollment(userId, null, null, bundleId,
                ENROLLMENT_SOURCE_INDIVIDUAL, null, requestDTO);
    }

    private Enrollment handleGroupCourseEnrollment(Long groupId, Long courseId, EnrollmentRequestDTO requestDTO) {
        // Check for existing group enrollment
        Optional<Enrollment> existingEnrollment = enrollmentRepository
                .findActiveEnrollmentByGroupAndCourse(groupId, courseId);

        if (existingEnrollment.isPresent()) {
            if (!requestDTO.isForceEnrollment()) {
                throw new ResourceAlreadyExistsException(
                        "Group " + groupId + " is already enrolled in course " + courseId);
            } else {
                Enrollment existing = existingEnrollment.get();
                updateEnrollmentDetails(existing, requestDTO);
                return enrollmentRepository.save(existing);
            }
        }

        return createEnrollment(null, groupId, courseId, null,
                ENROLLMENT_SOURCE_GROUP, null, requestDTO);
    }

    private Enrollment handleGroupBundleEnrollment(Long groupId, Long bundleId, EnrollmentRequestDTO requestDTO) {
        // Check for existing group bundle enrollment
        Optional<Enrollment> existingEnrollment = enrollmentRepository
                .findActiveEnrollmentByGroupAndBundle(groupId, bundleId);

        if (existingEnrollment.isPresent()) {
            if (!requestDTO.isForceEnrollment()) {
                throw new ResourceAlreadyExistsException(
                        "Group " + groupId + " is already enrolled in bundle " + bundleId);
            } else {
                Enrollment existing = existingEnrollment.get();
                updateEnrollmentDetails(existing, requestDTO);
                return enrollmentRepository.save(existing);
            }
        }

        return createEnrollment(null, groupId, null, bundleId,
                ENROLLMENT_SOURCE_GROUP, null, requestDTO);
    }

    private List<Enrollment> expandBundleToIndividualCourses(Enrollment bundleEnrollment, Long userId,
                                                             Long bundleId, EnrollmentRequestDTO requestDTO) {
        List<Enrollment> courseEnrollments = new ArrayList<>();
        List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();

        for (Long courseId : courseIds) {
            // Check if user already has a course enrollment with higher priority
            Optional<Enrollment> existingCourseEnrollment = enrollmentRepository
                    .findActiveEnrollmentByUserAndCourse(userId, courseId);

            if (existingCourseEnrollment.isPresent()) {
                Enrollment existing = existingCourseEnrollment.get();
                // If existing enrollment has lower priority (higher numeric value), update it
                if (shouldUpdateExistingEnrollment(existing.getEnrollmentSource(), ENROLLMENT_SOURCE_BUNDLE_EXPANSION)) {
                    updateEnrollmentDetails(existing, requestDTO);
                    existing.setParentEnrollmentId(bundleEnrollment.getEnrollmentId());
                    existing.setEnrollmentSource(ENROLLMENT_SOURCE_BUNDLE_EXPANSION);
                    courseEnrollments.add(enrollmentRepository.save(existing));
                }
                // If existing has higher priority, skip creating new enrollment
            } else {
                // Create new course enrollment from bundle expansion
                Enrollment courseEnrollment = createEnrollment(
                        userId, null, courseId, null,
                        ENROLLMENT_SOURCE_BUNDLE_EXPANSION, bundleEnrollment.getEnrollmentId(),
                        requestDTO);
                courseEnrollments.add(courseEnrollment);
            }
        }

        return courseEnrollments;
    }

    private List<Enrollment> createIndividualEnrollmentsForGroupMembers(Enrollment groupEnrollment,
                                                                        Long groupId, Long courseId,
                                                                        EnrollmentRequestDTO requestDTO) {
        List<Enrollment> memberEnrollments = new ArrayList<>();
        List<Long> memberIds = userGroupRepository.findUserIdsByGroupId(groupId);

        for (Long memberId : memberIds) {
            // Check if member already has enrollment for this course
            Optional<Enrollment> existingEnrollment = enrollmentRepository
                    .findActiveEnrollmentByUserAndCourse(memberId, courseId);

            if (existingEnrollment.isPresent()) {
                Enrollment existing = existingEnrollment.get();
                // Update if group enrollment has higher priority
                if (shouldUpdateExistingEnrollment(existing.getEnrollmentSource(), ENROLLMENT_SOURCE_GROUP)) {
                    updateEnrollmentDetails(existing, requestDTO);
                    existing.setParentEnrollmentId(groupEnrollment.getEnrollmentId());
                    existing.setEnrollmentSource(ENROLLMENT_SOURCE_GROUP);
                    memberEnrollments.add(enrollmentRepository.save(existing));
                }
            } else {
                // Create new enrollment for group member
                Enrollment memberEnrollment = createEnrollment(
                        memberId, null, courseId, null,
                        ENROLLMENT_SOURCE_GROUP, groupEnrollment.getEnrollmentId(),
                        requestDTO);
                memberEnrollments.add(memberEnrollment);
            }
        }

        return memberEnrollments;
    }

    private Enrollment createEnrollment(Long userId, Long groupId, Long courseId, Long bundleId,
                                        String enrollmentSource, Long parentEnrollmentId,
                                        EnrollmentRequestDTO requestDTO) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setGroupId(groupId);
        enrollment.setCourseId(courseId);
        enrollment.setBundleId(bundleId);
        enrollment.setAssignedBy(requestDTO.getAssignedBy());
        enrollment.setAssignedAt(LocalDateTime.now());
        enrollment.setDeadline(requestDTO.getDeadline());
        enrollment.setStatus(requestDTO.getStatus());
        enrollment.setEnrollmentSource(enrollmentSource);
        enrollment.setParentEnrollmentId(parentEnrollmentId);
        enrollment.setProgressPercentage(BigDecimal.valueOf(0.0));
        enrollment.setCreatedAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
        enrollment.setActive(true);

        return enrollmentRepository.save(enrollment);
    }

    private void updateEnrollmentDetails(Enrollment enrollment, EnrollmentRequestDTO requestDTO) {
        enrollment.setAssignedBy(requestDTO.getAssignedBy());
        enrollment.setAssignedAt(LocalDateTime.now());
        enrollment.setDeadline(requestDTO.getDeadline());
        enrollment.setStatus(requestDTO.getStatus());
        enrollment.setUpdatedAt(LocalDateTime.now());
    }

    private boolean shouldUpdateExistingEnrollment(String existingSource, String newSource) {
        // Priority order: INDIVIDUAL (1) > GROUP (2) > BUNDLE_EXPANSION (3)
        int existingPriority = getSourcePriority(existingSource);
        int newPriority = getSourcePriority(newSource);

        return newPriority < existingPriority;
    }

    private int getSourcePriority(String source) {
        return switch (source) {
            case ENROLLMENT_SOURCE_INDIVIDUAL -> 1;
            case ENROLLMENT_SOURCE_GROUP -> 2;
            case ENROLLMENT_SOURCE_BUNDLE_EXPANSION -> 3;
            default -> 4;
        };
    }
}