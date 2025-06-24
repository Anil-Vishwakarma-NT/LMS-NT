package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.entities.*;
import com.nt.LMS.exception.ResourceAlreadyExistsException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.ResourceNotValidException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.*;
import com.nt.LMS.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final String ENROLLMENT_SOURCE_BUNDLE = "BUNDLE";
    private static final String ENROLLMENT_SOURCE_GROUP_BUNDLE = "GROUP_BUNDLE";

    @Override
    public List<EnrollmentOutDTO> enroll(EnrollmentRequestInDTO requestDTO) {
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

            return createdEnrollments.stream()
                    .map(this::convertToEnrollmentOutDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResourceNotValidException("Enrollment failed: " + e.getMessage());
        }
    }

    private List<Enrollment> enrollUsersToCourses(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long courseId : requestDTO.getCourseIds()) {
                // Always create enrollment - no duplicate checking
                Enrollment enrollment = createEnrollment(
                        userId, null, courseId, null,
                        ENROLLMENT_SOURCE_INDIVIDUAL, requestDTO
                );
                enrollments.add(enrollment);
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollUsersToBundles(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long bundleId : requestDTO.getBundleIds()) {
                // Get courses in the bundle
                List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();

                // Create enrollment for each course in the bundle
                for (Long courseId : courseIds) {
                    Enrollment enrollment = createEnrollment(
                            userId, null, courseId, bundleId,
                            ENROLLMENT_SOURCE_BUNDLE, requestDTO
                    );
                    enrollments.add(enrollment);
                }
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollGroupsToCourses(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            // Get all users from the group
            List<Long> memberIds = userGroupRepository.findUserIdsByGroupId(groupId);

            for (Long courseId : requestDTO.getCourseIds()) {
                // Create enrollment for each member in the group
                for (Long memberId : memberIds) {
                    Enrollment enrollment = createEnrollment(
                            memberId, groupId, courseId, null,
                            ENROLLMENT_SOURCE_GROUP, requestDTO
                    );
                    enrollments.add(enrollment);
                }
            }
        }

        return enrollments;
    }

    private List<Enrollment> enrollGroupsToBundles(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            // Get all users from the group
            List<Long> memberIds = userGroupRepository.findUserIdsByGroupId(groupId);

            for (Long bundleId : requestDTO.getBundleIds()) {
                // Get all courses in the bundle
                List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();

                // Create enrollment for each user-course combination
                for (Long memberId : memberIds) {
                    for (Long courseId : courseIds) {
                        Enrollment enrollment = createEnrollment(
                                memberId, groupId, courseId, bundleId,
                                ENROLLMENT_SOURCE_GROUP_BUNDLE, requestDTO
                        );
                        enrollments.add(enrollment);
                    }
                }
            }
        }

        return enrollments;
    }

    private Enrollment createEnrollment(Long userId, Long groupId, Long courseId, Long bundleId,
                                        String enrollmentSource, EnrollmentRequestInDTO requestDTO) {
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
        enrollment.setCreatedAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
        enrollment.setActive(true);

        return enrollmentRepository.save(enrollment);
    }

    private EnrollmentOutDTO convertToEnrollmentOutDTO(Enrollment enrollment) {
        EnrollmentOutDTO dto = new EnrollmentOutDTO();

        dto.setEnrollmentId(enrollment.getEnrollmentId());
        dto.setUserId(enrollment.getUserId());
        dto.setGroupId(enrollment.getGroupId());
        dto.setCourseId(enrollment.getCourseId());
        dto.setBundleId(enrollment.getBundleId());
        dto.setAssignedBy(enrollment.getAssignedBy());
        dto.setAssignedAt(enrollment.getAssignedAt());
        dto.setDeadline(enrollment.getDeadline());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrollmentSource(enrollment.getEnrollmentSource());
        dto.setStartedAt(enrollment.getStartedAt());
        dto.setCompletedAt(enrollment.getCompletedAt());
        dto.setCreatedAt(enrollment.getCreatedAt());
        dto.setUpdatedAt(enrollment.getUpdatedAt());
        dto.setIsActive(enrollment.getIsActive());

        return dto;
    }

    private void validateEnrollmentRequest(EnrollmentRequestInDTO requestDTO) {
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

    @Override
    public EnrollmentDashBoardStatsOutDTO getEnrollmentStats() {
        EnrollmentDashBoardStatsOutDTO stats = new EnrollmentDashBoardStatsOutDTO();

        // Total active enrollments
        stats.setTotalEnrollments(enrollmentRepository.countByIsActiveTrue());

        // Individual users enrolled (distinct users with active enrollments)
        stats.setIndividualUsersEnrolled(enrollmentRepository.countDistinctUsersByIsActiveTrue());

        // Groups enrolled (distinct groups with active enrollments)
        stats.setGroupsEnrolled(enrollmentRepository.countDistinctGroupsByIsActiveTrue());

        // Bundles enrolled (distinct bundles with active enrollments)
        stats.setBundlesEnrolled(enrollmentRepository.countDistinctBundlesByIsActiveTrue());

        // Top enrolled course (course with most active enrollments)
        Long topCourseId = enrollmentRepository.findTopEnrolledCourse();
        if (topCourseId != null) {
            // Get course name from microservice
            String courseName = courseMicroserviceClient.getCourseNameById(topCourseId).getBody();
            stats.setTopEnrolledCourse(courseName != null ? courseName : "Course ID: " + topCourseId);
        } else {
            stats.setTopEnrolledCourse("No courses enrolled");
        }

        // Average progress percentage across all active enrollments
        BigDecimal avgProgress = enrollmentRepository.findAverageProgressPercentage();
        stats.setAverageProgressPercentage(avgProgress != null ? avgProgress.longValue() : 0L);

        // Count of enrollments with deadlines due (deadline before current time)
        stats.setDueDeadlines(enrollmentRepository.countEnrollmentsWithDueDeadlines(LocalDateTime.now()));

        return stats;
    }

    @Override
    public UserEnrollmentsOutDTO getUserEnrollmentsByUserID(Long userId) {
        // Validate user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        UserEnrollmentsOutDTO userEnrollmentsDTO = new UserEnrollmentsOutDTO();

        // Set basic user information
        userEnrollmentsDTO.setUserId(userId);
        userEnrollmentsDTO.setUserName(user.getFirstName() + " " + user.getLastName());
        userEnrollmentsDTO.setStatus(user.isActive());

        // Get all active enrollments for the user
        List<Enrollment> userEnrollments = enrollmentRepository.findActiveEnrollmentsByUserId(userId);

        // Separate individual course enrollments (not from bundles)
        List<Enrollment> individualCourseEnrollments = userEnrollments.stream()
                .filter(enrollment -> enrollment.getCourseId() != null &&
                        enrollment.getBundleId() == null &&
                        ENROLLMENT_SOURCE_INDIVIDUAL.equals(enrollment.getEnrollmentSource()))
                .collect(Collectors.toList());

        // Get bundle enrollments (actual bundle enrollments, not expanded courses)
        List<Enrollment> bundleEnrollments = userEnrollments.stream()
                .filter(enrollment -> enrollment.getBundleId() != null &&
                        enrollment.getCourseId() == null)
                .collect(Collectors.toList());

        // Set enrollment counts
        userEnrollmentsDTO.setCourseEnrollments((long) individualCourseEnrollments.size());
        userEnrollmentsDTO.setBundleEnrollments((long) bundleEnrollments.size());

        // Calculate total unique courses (individual + from bundles)
        Set<Long> uniqueCourseIds = new HashSet<>();

        // Add individual courses
        individualCourseEnrollments.forEach(enrollment -> uniqueCourseIds.add(enrollment.getCourseId()));

        // Add courses from bundles
        for (Enrollment bundleEnrollment : bundleEnrollments) {
            List<Long> bundleCourseIds = courseMicroserviceClient
                    .findCourseIdsByBundleId(bundleEnrollment.getBundleId()).getBody();
            if (bundleCourseIds != null) {
                uniqueCourseIds.addAll(bundleCourseIds);
            }
        }
        userEnrollmentsDTO.setTotalCourses((long) uniqueCourseIds.size());

        // Calculate average completion percentage across all enrollments
        List<BigDecimal> progressValues = userEnrollments.stream()
                .map(Enrollment::getProgressPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!progressValues.isEmpty()) {
            BigDecimal averageProgress = progressValues.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(progressValues.size()), 2, RoundingMode.HALF_UP);
            userEnrollmentsDTO.setAverageCompletion(averageProgress.floatValue());
        } else {
            userEnrollmentsDTO.setAverageCompletion(0.0f);
        }

        // Count upcoming deadlines (deadlines in the future)
        LocalDateTime now = LocalDateTime.now();
        int upcomingDeadlines = (int) userEnrollments.stream()
                .filter(enrollment -> enrollment.getDeadline() != null && enrollment.getDeadline().isAfter(now))
                .count();
        userEnrollmentsDTO.setUpcomingDeadlines(upcomingDeadlines);

        // Build individual enrolled courses list
        List<EnrolledCoursesOutDTO> enrolledCoursesList = buildIndividualEnrolledCoursesList(individualCourseEnrollments);
        userEnrollmentsDTO.setEnrolledCoursesList(enrolledCoursesList);

        // Build enrolled bundles list with their courses
        List<EnrolledBundlesOutDTO> enrolledBundlesList = buildEnrolledBundlesListWithCourses(bundleEnrollments, userId);
        userEnrollmentsDTO.setEnrolledBundlesList(enrolledBundlesList);

        return userEnrollmentsDTO;
    }

    @Override
    public List<UserEnrollmentsOutDTO> getAllUsersEnrollments() {
        // Get all users who have active enrollments
        List<Long> userIdsWithEnrollments = enrollmentRepository.findDistinctUserIdsWithActiveEnrollments();

        List<UserEnrollmentsOutDTO> allUserEnrollments = new ArrayList<>();

        for (Long userId : userIdsWithEnrollments) {
            try {
                UserEnrollmentsOutDTO userEnrollmentDTO = buildUserEnrollmentDTO(userId);
                allUserEnrollments.add(userEnrollmentDTO);
            } catch (Exception e) {
                // Log the error but continue processing other users
                System.err.println("Error processing enrollments for user " + userId + ": " + e.getMessage());
            }
        }

        return allUserEnrollments;
    }

    private UserEnrollmentsOutDTO buildUserEnrollmentDTO(Long userId) {
        // Get user information
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        UserEnrollmentsOutDTO userEnrollmentsDTO = new UserEnrollmentsOutDTO();

        // Set basic user information
        userEnrollmentsDTO.setUserId(userId);
        userEnrollmentsDTO.setUserName(user.getFirstName() + " " + user.getLastName());
        userEnrollmentsDTO.setStatus(user.isActive());

        // Get all active enrollments for the user
        List<Enrollment> userEnrollments = enrollmentRepository.findActiveEnrollmentsByUserId(userId);

        // Separate individual course enrollments (not from bundles)
        List<Enrollment> individualCourseEnrollments = userEnrollments.stream()
                .filter(enrollment -> enrollment.getCourseId() != null &&
                        enrollment.getBundleId() == null &&
                        ENROLLMENT_SOURCE_INDIVIDUAL.equals(enrollment.getEnrollmentSource()))
                .collect(Collectors.toList());

        // Get bundle enrollments (actual bundle enrollments, not expanded courses)
        List<Enrollment> bundleEnrollments = userEnrollments.stream()
                .filter(enrollment -> enrollment.getBundleId() != null &&
                        enrollment.getCourseId() == null)
                .collect(Collectors.toList());

        // Set enrollment counts
        userEnrollmentsDTO.setCourseEnrollments((long) individualCourseEnrollments.size());
        userEnrollmentsDTO.setBundleEnrollments((long) bundleEnrollments.size());

        // Calculate total unique courses (individual + from bundles)
        Set<Long> uniqueCourseIds = new HashSet<>();

        // Add individual courses
        individualCourseEnrollments.forEach(enrollment -> uniqueCourseIds.add(enrollment.getCourseId()));

        // Add courses from bundles
        for (Enrollment bundleEnrollment : bundleEnrollments) {
            try {
                List<Long> bundleCourseIds = courseMicroserviceClient
                        .findCourseIdsByBundleId(bundleEnrollment.getBundleId()).getBody();
                if (bundleCourseIds != null) {
                    uniqueCourseIds.addAll(bundleCourseIds);
                }
            } catch (Exception e) {
                System.err.println("Error fetching courses for bundle " + bundleEnrollment.getBundleId() + ": " + e.getMessage());
            }
        }
        userEnrollmentsDTO.setTotalCourses((long) uniqueCourseIds.size());

        // Calculate average completion percentage across all enrollments
        List<BigDecimal> progressValues = userEnrollments.stream()
                .map(Enrollment::getProgressPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!progressValues.isEmpty()) {
            BigDecimal averageProgress = progressValues.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(progressValues.size()), 2, RoundingMode.HALF_UP);
            userEnrollmentsDTO.setAverageCompletion(averageProgress.floatValue());
        } else {
            userEnrollmentsDTO.setAverageCompletion(0.0f);
        }

        // Count upcoming deadlines (deadlines in the future)
        LocalDateTime now = LocalDateTime.now();
        int upcomingDeadlines = (int) userEnrollments.stream()
                .filter(enrollment -> enrollment.getDeadline() != null && enrollment.getDeadline().isAfter(now))
                .count();
        userEnrollmentsDTO.setUpcomingDeadlines(upcomingDeadlines);

        // Build individual enrolled courses list
        List<EnrolledCoursesOutDTO> enrolledCoursesList = buildIndividualEnrolledCoursesList(individualCourseEnrollments);
        userEnrollmentsDTO.setEnrolledCoursesList(enrolledCoursesList);

        // Build enrolled bundles list with their courses
        List<EnrolledBundlesOutDTO> enrolledBundlesList = buildEnrolledBundlesListWithCourses(bundleEnrollments, userId);
        userEnrollmentsDTO.setEnrolledBundlesList(enrolledBundlesList);

        return userEnrollmentsDTO;
    }

    private List<EnrolledCoursesOutDTO> buildIndividualEnrolledCoursesList(List<Enrollment> individualCourseEnrollments) {
        List<EnrolledCoursesOutDTO> enrolledCoursesList = new ArrayList<>();

        for (Enrollment enrollment : individualCourseEnrollments) {
            EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
            courseDTO.setCourseId(enrollment.getCourseId());

            try {
                // Get course name from microservice
                String courseName = courseMicroserviceClient.getCourseNameById(enrollment.getCourseId()).getBody();
                courseDTO.setCourseName(courseName != null ? courseName : "Course ID: " + enrollment.getCourseId());
            } catch (Exception e) {
                courseDTO.setCourseName("Course ID: " + enrollment.getCourseId());
                System.err.println("Error fetching course name for course " + enrollment.getCourseId() + ": " + e.getMessage());
            }

            courseDTO.setProgress(enrollment.getProgressPercentage() != null ?
                    enrollment.getProgressPercentage().floatValue() : 0.0f);
            courseDTO.setEnrollmentDate(enrollment.getAssignedAt());
            courseDTO.setDeadline(enrollment.getDeadline());

            enrolledCoursesList.add(courseDTO);
        }

        return enrolledCoursesList;
    }

    private List<EnrolledBundlesOutDTO> buildEnrolledBundlesListWithCourses(List<Enrollment> bundleEnrollments, Long userId) {
        List<EnrolledBundlesOutDTO> enrolledBundlesList = new ArrayList<>();
        Map<Long, List<Enrollment>> bundleEnrollmentMap = bundleEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getBundleId));

        for (Map.Entry<Long, List<Enrollment>> entry : bundleEnrollmentMap.entrySet()) {
            Long bundleId = entry.getKey();
            List<Enrollment> enrollmentsForBundle = entry.getValue();

            // Use the first enrollment for basic info (they should have same bundle info)
            Enrollment primaryEnrollment = enrollmentsForBundle.get(0);

            EnrolledBundlesOutDTO bundleDTO = new EnrolledBundlesOutDTO();
            bundleDTO.setBundleId(bundleId);

            try {
                // Get bundle name from microservice
                String bundleName = courseMicroserviceClient.getBundleNameById(bundleId).getBody().getData();
                bundleDTO.setBundleName(bundleName != null ? bundleName : "Bundle ID: " + bundleId);
            } catch (Exception e) {
                bundleDTO.setBundleName("Bundle ID: " + bundleId);
                System.err.println("Error fetching bundle name for bundle " + bundleId + ": " + e.getMessage());
            }

            // Get all course enrollments from this bundle for this user
            List<Enrollment> bundleCourseEnrollments = enrollmentRepository
                    .findActiveEnrollmentsByUserIdAndParentEnrollmentId(userId, primaryEnrollment.getEnrollmentId());

            // Calculate bundle progress based on course enrollments from this bundle
            List<BigDecimal> bundleProgressValues = bundleCourseEnrollments.stream()
                    .map(Enrollment::getProgressPercentage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!bundleProgressValues.isEmpty()) {
                BigDecimal bundleProgress = bundleProgressValues.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(bundleProgressValues.size()), 2, RoundingMode.HALF_UP);
                bundleDTO.setProgress(bundleProgress.floatValue());
            } else {
                bundleDTO.setProgress(0.0f);
            }

            bundleDTO.setEnrollmentDate(primaryEnrollment.getAssignedAt());
            bundleDTO.setDeadline(primaryEnrollment.getDeadline());

            // Build courses list for this bundle
            List<EnrolledCoursesOutDTO> bundleCoursesList = new ArrayList<>();
            for (Enrollment courseEnrollment : bundleCourseEnrollments) {
                EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
                courseDTO.setCourseId(courseEnrollment.getCourseId());

                try {
                    String courseName = courseMicroserviceClient.getCourseNameById(courseEnrollment.getCourseId()).getBody();
                    courseDTO.setCourseName(courseName != null ? courseName : "Course ID: " + courseEnrollment.getCourseId());
                } catch (Exception e) {
                    courseDTO.setCourseName("Course ID: " + courseEnrollment.getCourseId());
                    System.err.println("Error fetching course name for course " + courseEnrollment.getCourseId() + ": " + e.getMessage());
                }

                courseDTO.setProgress(courseEnrollment.getProgressPercentage() != null ?
                        courseEnrollment.getProgressPercentage().floatValue() : 0.0f);
                courseDTO.setEnrollmentDate(courseEnrollment.getAssignedAt());
                courseDTO.setDeadline(courseEnrollment.getDeadline());

                bundleCoursesList.add(courseDTO);
            }

            bundleDTO.setEnrolledCoursesList(bundleCoursesList);
            enrolledBundlesList.add(bundleDTO);
        }

        return enrolledBundlesList;
    }

    @Override
    public List<UserCourseEnrollmentOutDTO> getIndividualCourseEnrollments() {
        try {
            // Get all individual course enrollments (not from bundles)
            List<Enrollment> individualCourseEnrollments = enrollmentRepository
                    .findAllActiveIndividualCourseEnrollments();

            // Group enrollments by course ID
            Map<Long, List<Enrollment>> enrollmentsByCourse = individualCourseEnrollments.stream()
                    .collect(Collectors.groupingBy(Enrollment::getCourseId));

            List<UserCourseEnrollmentOutDTO> result = new ArrayList<>();

            for (Map.Entry<Long, List<Enrollment>> entry : enrollmentsByCourse.entrySet()) {
                Long courseId = entry.getKey();
                List<Enrollment> courseEnrollments = entry.getValue();

                UserCourseEnrollmentOutDTO courseEnrollmentDTO = new UserCourseEnrollmentOutDTO();

                // Set course basic information
                courseEnrollmentDTO.setCourseId(courseId);
                courseEnrollmentDTO.setIndividualEnrollments((long) courseEnrollments.size());

                // Get course information from microservice
                try {
                    CourseInfoOutDTO courseInfo = getCourseInfoById(courseId);
                    if (courseInfo != null) {
                        courseEnrollmentDTO.setCourseName(courseInfo.getTitle());
                        courseEnrollmentDTO.setOwnerId(courseInfo.getOwnerId());
                        courseEnrollmentDTO.setActive(courseInfo.isActive());

                        // Get owner name
                        Optional<User> ownerOptional = userRepository.findById(courseInfo.getOwnerId());
                        if (ownerOptional.isPresent()) {
                            User owner = ownerOptional.get();
                            courseEnrollmentDTO.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
                        } else {
                            courseEnrollmentDTO.setOwnerName("Owner ID: " + courseInfo.getOwnerId());
                        }
                    } else {
                        courseEnrollmentDTO.setCourseName("Course ID: " + courseId);
                        courseEnrollmentDTO.setOwnerId(null);
                        courseEnrollmentDTO.setOwnerName("Unknown");
                        courseEnrollmentDTO.setActive(false);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching course info for course " + courseId + ": " + e.getMessage());
                    courseEnrollmentDTO.setCourseName("Course ID: " + courseId);
                    courseEnrollmentDTO.setOwnerId(null);
                    courseEnrollmentDTO.setOwnerName("Unknown");
                    courseEnrollmentDTO.setActive(false);
                }

                // Build enrolled users list
                List<EnrolledUserOutDTO> enrolledUsers = buildEnrolledUsersList(courseEnrollments);
                courseEnrollmentDTO.setEnrolledUserOutDTOList(enrolledUsers);

                result.add(courseEnrollmentDTO);
            }

            // Sort by course name for consistent ordering
            result.sort(Comparator.comparing(UserCourseEnrollmentOutDTO::getCourseName));

            return result;

        } catch (Exception e) {
            throw new ResourceNotValidException("Failed to retrieve individual course enrollments: " + e.getMessage());
        }
    }

    private CourseInfoOutDTO getCourseInfoById(Long courseId) {
        try {
            ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response =
                    courseMicroserviceClient.getCourseInfo();

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().stream()
                        .filter(course -> course.getCourseId().equals(courseId))
                        .findFirst()
                        .orElse(null);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error fetching course info: " + e.getMessage());
            return null;
        }
    }

    private List<EnrolledUserOutDTO> buildEnrolledUsersList(List<Enrollment> enrollments) {
        List<EnrolledUserOutDTO> enrolledUsers = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            EnrolledUserOutDTO userDTO = new EnrolledUserOutDTO();

            // Set user information
            userDTO.setUserId(enrollment.getUserId());

            // Get user name
            Optional<User> userOptional = userRepository.findById(enrollment.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userDTO.setUserName(user.getFirstName() + " " + user.getLastName());
            } else {
                userDTO.setUserName("User ID: " + enrollment.getUserId());
            }

            // Get assigned by user name
            Optional<User> assignedByOptional = userRepository.findById(enrollment.getAssignedBy());
            if (assignedByOptional.isPresent()) {
                User assignedBy = assignedByOptional.get();
                userDTO.setAssignedByName(assignedBy.getFirstName() + " " + assignedBy.getLastName());
            } else {
                userDTO.setAssignedByName("User ID: " + enrollment.getAssignedBy());
            }

            // Set enrollment details
            userDTO.setProgress(enrollment.getProgressPercentage() != null ?
                    enrollment.getProgressPercentage().doubleValue() : 0.0);
            userDTO.setEnrollmentDate(enrollment.getAssignedAt());
            userDTO.setDeadline(enrollment.getDeadline());

            enrolledUsers.add(userDTO);
        }

        // Sort by user name for consistent ordering
        enrolledUsers.sort(Comparator.comparing(EnrolledUserOutDTO::getUserName));

        return enrolledUsers;
    }

    // Add this method to your EnrollmentServiceImpl class

    @Override
    public List<UserBundleEnrollmentOutDTO> getIndividualBundleEnrollments() {
        // Get all active bundle enrollments (where bundleId is not null and courseId is null)
        List<Enrollment> bundleEnrollments = enrollmentRepository.findAllActiveIndividualBundleEnrollments();

        // Group enrollments by bundleId
        Map<Long, List<Enrollment>> enrollmentsByBundle = bundleEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getBundleId));

        List<UserBundleEnrollmentOutDTO> result = new ArrayList<>();

        for (Map.Entry<Long, List<Enrollment>> entry : enrollmentsByBundle.entrySet()) {
            Long bundleId = entry.getKey();
            List<Enrollment> enrollments = entry.getValue();

            UserBundleEnrollmentOutDTO bundleDTO = buildUserBundleEnrollmentDTO(bundleId, enrollments);
            result.add(bundleDTO);
        }

        return result;
    }

    private UserBundleEnrollmentOutDTO buildUserBundleEnrollmentDTO(Long bundleId, List<Enrollment> enrollments) {
        UserBundleEnrollmentOutDTO bundleDTO = new UserBundleEnrollmentOutDTO();
        bundleDTO.setBundleId(bundleId);
        // Get bundle name from microservice
        String bundleName = courseMicroserviceClient.getBundleNameById(bundleId).getBody().getData();
        bundleDTO.setBundleName(bundleName != null ? bundleName : "Bundle ID: " + bundleId);

        // Get total courses in bundle from microservice
        List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();
        bundleDTO.setTotalCourses(courseIds != null ? (long) courseIds.size() : 0L);

        // Set individual enrollments count
        bundleDTO.setIndividualEnrollments((long) enrollments.size());

        // Calculate average completion across all enrollments for this bundle
        List<BigDecimal> progressValues = enrollments.stream()
                .map(Enrollment::getProgressPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!progressValues.isEmpty()) {
            BigDecimal averageProgress = progressValues.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(progressValues.size()), 2, RoundingMode.HALF_UP);
            bundleDTO.setAverageCompletion(averageProgress.floatValue());
        } else {
            bundleDTO.setAverageCompletion(0.0f);
        }

        // Set active status (true if any enrollment is active)
        bundleDTO.setActive(enrollments.stream().anyMatch(Enrollment::getActive));

        // Build enrolled users list
        List<EnrolledUserOutDTO> enrolledUsers = buildEnrolledUsersListForBundle(enrollments);
        bundleDTO.setEnrolledUserOutDTOList(enrolledUsers);

        return bundleDTO;
    }

    private List<EnrolledUserOutDTO> buildEnrolledUsersListForBundle(List<Enrollment> enrollments) {
        List<EnrolledUserOutDTO> enrolledUsers = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            EnrolledUserOutDTO userDTO = new EnrolledUserOutDTO();

            // Set user ID
            userDTO.setUserId(enrollment.getUserId());

            // Get user name
            Optional<User> userOptional = userRepository.findById(enrollment.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userDTO.setUserName(user.getFirstName() + " " + user.getLastName());
            } else {
                userDTO.setUserName("User ID: " + enrollment.getUserId());
            }

            // Get assigned by user name
            Optional<User> assignedByUserOptional = userRepository.findById(enrollment.getAssignedBy());
            if (assignedByUserOptional.isPresent()) {
                User assignedByUser = assignedByUserOptional.get();
                userDTO.setAssignedByName(assignedByUser.getFirstName() + " " + assignedByUser.getLastName());
            } else {
                userDTO.setAssignedByName("User ID: " + enrollment.getAssignedBy());
            }

            // Set progress (convert BigDecimal to Double)
            userDTO.setProgress(enrollment.getProgressPercentage() != null ?
                    enrollment.getProgressPercentage().doubleValue() : 0.0);

            // Set enrollment date
            userDTO.setEnrollmentDate(enrollment.getAssignedAt());

            // Set deadline
            userDTO.setDeadline(enrollment.getDeadline());

            enrolledUsers.add(userDTO);
        }

        return enrolledUsers;
    }

    @Override
    public List<UserCourseEnrollDetails> getUserEnrolledCourses(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndIsActiveTrue(userId);

        Map<Long, Enrollment> earliestCourseEnrollments = new HashMap<>();

        for (Enrollment e : enrollments) {
            Long courseId = e.getCourseId();
            if (courseId == null) continue;

            if (!earliestCourseEnrollments.containsKey(courseId) ||
                    e.getAssignedAt().isBefore(earliestCourseEnrollments.get(courseId).getAssignedAt())) {
                earliestCourseEnrollments.put(courseId, e);
            }
        }

        return earliestCourseEnrollments.values().stream()
                .map(e -> {
                    UserCourseEnrollDetails dto = new UserCourseEnrollDetails();
                    dto.setCourseId(e.getCourseId());
                    dto.setAssignedById(e.getAssignedBy()); // assuming it's Long
                    dto.setEnrollmentDate(e.getAssignedAt());
                    dto.setDeadline(e.getDeadline());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}