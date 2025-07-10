package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.user_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.user_service_lms.dto.outDTO.EnrolledBundlesOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrolledCoursesOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrolledUserOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentDashBoardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserBundleEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserEnrollmentsOutDTO;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.exception.ResourceNotValidException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.GroupRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    /**
     * Repository for managing Enrollment entities.
     */
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Repository for managing User entities.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for managing Group entities.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Feign client for communicating with the Course microservice.
     */
    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;
    /**
     * Repository for managing User-Group relationships.
     */
    @Autowired
    private UserGroupRepository userGroupRepository;

    /**
     * Enrollment source constant for individual enrollments.
     */
    private static final String ENROLLMENT_SOURCE_INDIVIDUAL = "INDIVIDUAL";
    /**
     * Enrollment source constant for group enrollments.
     */
    private static final String ENROLLMENT_SOURCE_GROUP = "GROUP";
    /**
     * Enrollment source constant for bundle enrollments.
     */
    private static final String ENROLLMENT_SOURCE_BUNDLE = "BUNDLE";
    /**
     * Enrollment source constant for group-bundle enrollments.
     */
    private static final String ENROLLMENT_SOURCE_GROUP_BUNDLE = "GROUP_BUNDLE";

    @Override
    public List<EnrollmentOutDTO> enroll(EnrollmentRequestInDTO requestDTO) {
        // Validate request
        validateEnrollmentRequest(requestDTO);

        List<Enrollment> createdEnrollments = new ArrayList<>();

        try {
            if (requestDTO.hasUsers()) {
                if (requestDTO.hasCourses()) {
                    // User to Course enrollment - INDIVIDUAL
                    createdEnrollments.addAll(enrollUsersToCourses(requestDTO));
                } else if (requestDTO.hasBundles()) {
                    // User to Bundle enrollment - BUNDLE (create rows for each course in bundle)
                    createdEnrollments.addAll(enrollUsersToBundles(requestDTO));
                }
            } else if (requestDTO.hasGroups()) {
                if (requestDTO.hasCourses()) {
                    // Group to Course enrollment - GROUP (create rows for each user in group)
                    createdEnrollments.addAll(enrollGroupsToCourses(requestDTO));
                } else if (requestDTO.hasBundles()) {
                    // Group to Bundle enrollment - GROUP_BUNDLE (create rows for each user-course combination)
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

    @Override
    public EnrollmentDashBoardStatsOutDTO getEnrollmentStats() {
        try {
            // Get all enrollments
            List<Enrollment> allEnrollments = enrollmentRepository.findAll();

            if (allEnrollments.isEmpty()) {
                return createEmptyStats();
            }

            // Calculate basic counts
            long totalEnrollments = allEnrollments.size();
            long activeEnrollments = allEnrollments.stream()
                    .mapToLong(e -> Boolean.TRUE.equals(e.getIsActive()) ? 1 : 0)
                    .sum();
            long inactiveEnrollments = totalEnrollments - activeEnrollments;

            // Calculate status-based counts
            Map<String, Long> statusCounts = allEnrollments.stream()
                    .collect(Collectors.groupingBy(
                            Enrollment::getStatus,
                            Collectors.counting()
                    ));

            long pendingEnrollments = statusCounts.getOrDefault("PENDING", 0L);
            long inProgressEnrollments = statusCounts.getOrDefault("IN_PROGRESS", 0L);
            long completedEnrollments = statusCounts.getOrDefault("COMPLETED", 0L);
            long expiredEnrollments = statusCounts.getOrDefault("EXPIRED", 0L);

            // Calculate source-based counts
            Map<String, Long> sourceCounts = allEnrollments.stream()
                    .collect(Collectors.groupingBy(
                            Enrollment::getEnrollmentSource,
                            Collectors.counting()
                    ));

            long individualEnrollments = sourceCounts.getOrDefault(ENROLLMENT_SOURCE_INDIVIDUAL, 0L);
            long groupEnrollments = sourceCounts.getOrDefault(ENROLLMENT_SOURCE_GROUP, 0L);
            long bundleEnrollments = sourceCounts.getOrDefault(ENROLLMENT_SOURCE_BUNDLE, 0L);
            long groupBundleEnrollments = sourceCounts.getOrDefault(ENROLLMENT_SOURCE_GROUP_BUNDLE, 0L);

            // Calculate unique counts
            long totalUniqueUsers = allEnrollments.stream()
                    .filter(e -> e.getUserId() != null)
                    .mapToLong(Enrollment::getUserId)
                    .distinct()
                    .count();

            long totalUniqueCourses = allEnrollments.stream()
                    .filter(e -> e.getCourseId() != null)
                    .mapToLong(Enrollment::getCourseId)
                    .distinct()
                    .count();

            long totalUniqueBundles = allEnrollments.stream()
                    .filter(e -> e.getBundleId() != null)
                    .mapToLong(Enrollment::getBundleId)
                    .distinct()
                    .count();

            long totalUniqueGroups = allEnrollments.stream()
                    .filter(e -> e.getGroupId() != null)
                    .mapToLong(Enrollment::getGroupId)
                    .distinct()
                    .count();

            // Calculate completion rates
            BigDecimal overallCompletionRate = calculateCompletionRate(completedEnrollments, totalEnrollments);

            List<Enrollment> individualEnrollmentsList = allEnrollments.stream()
                    .filter(e -> ENROLLMENT_SOURCE_INDIVIDUAL.equals(e.getEnrollmentSource()))
                    .collect(Collectors.toList());
            BigDecimal individualCompletionRate = calculateCompletionRateForList(individualEnrollmentsList);

            List<Enrollment> groupEnrollmentsList = allEnrollments.stream()
                    .filter(e -> ENROLLMENT_SOURCE_GROUP.equals(e.getEnrollmentSource()))
                    .collect(Collectors.toList());
            BigDecimal groupCompletionRate = calculateCompletionRateForList(groupEnrollmentsList);

            List<Enrollment> bundleEnrollmentsList = allEnrollments.stream()
                    .filter(e -> ENROLLMENT_SOURCE_BUNDLE.equals(e.getEnrollmentSource()) ||
                            ENROLLMENT_SOURCE_GROUP_BUNDLE.equals(e.getEnrollmentSource()))
                    .collect(Collectors.toList());
            BigDecimal bundleCompletionRate = calculateCompletionRateForList(bundleEnrollmentsList);

            // Calculate recent activity (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

            long recentEnrollments = allEnrollments.stream()
                    .mapToLong(e -> e.getCreatedAt().isAfter(thirtyDaysAgo) ? 1 : 0)
                    .sum();

            long recentCompletions = allEnrollments.stream()
                    .mapToLong(e -> e.getCompletedAt() != null && e.getCompletedAt().isAfter(thirtyDaysAgo) ? 1 : 0)
                    .sum();

            // Calculate status distribution percentages
            Map<String, BigDecimal> statusDistribution = calculatePercentageDistribution(statusCounts, totalEnrollments);

            // Calculate source distribution percentages
            Map<String, BigDecimal> sourceDistribution = calculatePercentageDistribution(sourceCounts, totalEnrollments);

            // Build and return the stats DTO
            return EnrollmentDashBoardStatsOutDTO.builder()
                    .totalEnrollments(totalEnrollments)
                    .activeEnrollments(activeEnrollments)
                    .inactiveEnrollments(inactiveEnrollments)
                    .pendingEnrollments(pendingEnrollments)
                    .inProgressEnrollments(inProgressEnrollments)
                    .completedEnrollments(completedEnrollments)
                    .expiredEnrollments(expiredEnrollments)
                    .individualEnrollments(individualEnrollments)
                    .groupEnrollments(groupEnrollments)
                    .bundleEnrollments(bundleEnrollments)
                    .groupBundleEnrollments(groupBundleEnrollments)
                    .totalUniqueUsers(totalUniqueUsers)
                    .totalUniqueCourses(totalUniqueCourses)
                    .totalUniqueBundles(totalUniqueBundles)
                    .totalUniqueGroups(totalUniqueGroups)
                    .overallCompletionRate(overallCompletionRate)
                    .individualCompletionRate(individualCompletionRate)
                    .groupCompletionRate(groupCompletionRate)
                    .bundleCompletionRate(bundleCompletionRate)
                    .recentEnrollments(recentEnrollments)
                    .recentCompletions(recentCompletions)
                    .statusDistribution(statusDistribution)
                    .sourceDistribution(sourceDistribution)
                    .build();

        } catch (Exception e) {
            throw new ResourceNotValidException("Failed to calculate enrollment statistics: " + e.getMessage());
        }
    }

    // Helper method to create empty stats when no enrollments exist
    private EnrollmentDashBoardStatsOutDTO createEmptyStats() {
        return EnrollmentDashBoardStatsOutDTO.builder()
                .totalEnrollments(0L)
                .activeEnrollments(0L)
                .inactiveEnrollments(0L)
                .pendingEnrollments(0L)
                .inProgressEnrollments(0L)
                .completedEnrollments(0L)
                .expiredEnrollments(0L)
                .individualEnrollments(0L)
                .groupEnrollments(0L)
                .bundleEnrollments(0L)
                .groupBundleEnrollments(0L)
                .totalUniqueUsers(0L)
                .totalUniqueCourses(0L)
                .totalUniqueBundles(0L)
                .totalUniqueGroups(0L)
                .overallCompletionRate(BigDecimal.ZERO)
                .individualCompletionRate(BigDecimal.ZERO)
                .groupCompletionRate(BigDecimal.ZERO)
                .bundleCompletionRate(BigDecimal.ZERO)
                .recentEnrollments(0L)
                .recentCompletions(0L)
                .statusDistribution(new HashMap<>())
                .sourceDistribution(new HashMap<>())
                .build();
    }

    // Helper method to calculate completion rate
    private BigDecimal calculateCompletionRate(long completed, long total) {
        if (total == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    // Helper method to calculate completion rate for a list of enrollments
    private BigDecimal calculateCompletionRateForList(List<Enrollment> enrollments) {
        if (enrollments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long completed = enrollments.stream()
                .mapToLong(e -> "COMPLETED".equals(e.getStatus()) ? 1 : 0)
                .sum();

        return calculateCompletionRate(completed, enrollments.size());
    }

    // Helper method to calculate percentage distribution
    private Map<String, BigDecimal> calculatePercentageDistribution(Map<String, Long> counts, long total) {
        if (total == 0) {
            return new HashMap<>();
        }

        Map<String, BigDecimal> distribution = new HashMap<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            BigDecimal percentage = BigDecimal.valueOf(entry.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            distribution.put(entry.getKey(), percentage);
        }

        return distribution;
    }

    @Override
    public UserEnrollmentsOutDTO getUserEnrollmentsByUserID(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get all active enrollments for the user
        List<Enrollment> userEnrollments = enrollmentRepository.findByUserIdAndIsActiveTrue(userId);

        if (userEnrollments.isEmpty()) {
            return createEmptyUserEnrollmentsDTO(userId, user.getUserName());
        }

        // Separate course and bundle enrollments
        List<Enrollment> courseEnrollments = userEnrollments.stream()
                .filter(e -> e.getCourseId() != null && e.getBundleId() == null)
                .collect(Collectors.toList());

        List<Enrollment> bundleEnrollments = userEnrollments.stream()
                .filter(e -> e.getBundleId() != null)
                .collect(Collectors.toList());

        // Process course enrollments
        List<EnrolledCoursesOutDTO> enrolledCoursesList = processCourseEnrollments(courseEnrollments);

        // Process bundle enrollments
        List<EnrolledBundlesOutDTO> enrolledBundlesList = processBundleEnrollments(bundleEnrollments);

        // Calculate statistics
        long totalCourses = calculateTotalCourses(enrolledCoursesList, enrolledBundlesList);
        float averageCompletion = calculateAverageCompletion(enrolledCoursesList, enrolledBundlesList);
        int upcomingDeadlines = calculateUpcomingDeadlines(userEnrollments);
        boolean status = determineUserStatus(userEnrollments);

        // Build and return DTO
        UserEnrollmentsOutDTO result = new UserEnrollmentsOutDTO();
        result.setUserId(userId);
        result.setUserName(user.getFirstName() + " " + user.getLastName());
        result.setCourseEnrollments((long) courseEnrollments.size());
        result.setBundleEnrollments((long) bundleEnrollments.size());
        result.setTotalCourses(totalCourses);
        result.setAverageCompletion(averageCompletion);
        result.setUpcomingDeadlines(upcomingDeadlines);
        result.setStatus(status);
        result.setEnrolledCoursesList(enrolledCoursesList);
        result.setEnrolledBundlesList(enrolledBundlesList);

        return result;
    }

    private UserEnrollmentsOutDTO createEmptyUserEnrollmentsDTO(Long userId, String userName) {
        UserEnrollmentsOutDTO result = new UserEnrollmentsOutDTO();
        result.setUserId(userId);
        result.setUserName(userName);
        result.setCourseEnrollments(0L);
        result.setBundleEnrollments(0L);
        result.setTotalCourses(0L);
        result.setAverageCompletion(0.0f);
        result.setUpcomingDeadlines(0);
        result.setStatus(true);
        result.setEnrolledCoursesList(new ArrayList<>());
        result.setEnrolledBundlesList(new ArrayList<>());
        return result;
    }

    private List<EnrolledCoursesOutDTO> processCourseEnrollments(List<Enrollment> courseEnrollments) {
        return courseEnrollments.stream().map(enrollment -> {
            EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
            courseDTO.setCourseId(enrollment.getCourseId());

            // Get course name
            try {
                ResponseEntity<String> courseNameResponse = courseMicroserviceClient.getCourseNameById(enrollment.getCourseId());
                courseDTO.setCourseName(courseNameResponse.getBody());
            } catch (Exception e) {
                courseDTO.setCourseName("Unknown Course");
            }

            // Get course progress
            try {
                CourseProgressWithMetaDTO progressData = courseMicroserviceClient.getCourseProgressWithMeta(
                        enrollment.getUserId(),
                        enrollment.getCourseId()
                );
                courseDTO.setProgress((float) progressData.getCourseCompletionPercentage());
            } catch (Exception e) {
                courseDTO.setProgress(0.0f);
            }

            courseDTO.setEnrollmentDate(enrollment.getAssignedAt());
            courseDTO.setDeadline(enrollment.getDeadline());

            return courseDTO;
        }).collect(Collectors.toList());
    }

    private List<EnrolledBundlesOutDTO> processBundleEnrollments(List<Enrollment> bundleEnrollments) {
        return bundleEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getBundleId))
                .entrySet().stream()
                .map(entry -> {
                    Long bundleId = entry.getKey();
                    List<Enrollment> bundleEnrollmentsList = entry.getValue();
                    Enrollment primaryEnrollment = bundleEnrollmentsList.get(0);

                    EnrolledBundlesOutDTO bundleDTO = new EnrolledBundlesOutDTO();
                    bundleDTO.setBundleId(bundleId);

                    // Get bundle name
                    try {
                        ResponseEntity<StandardResponseOutDTO<String>> bundleNameResponse =
                                courseMicroserviceClient.getBundleNameById(bundleId);
                        bundleDTO.setBundleName(bundleNameResponse.getBody().getData());
                    } catch (Exception e) {
                        bundleDTO.setBundleName("Unknown Bundle");
                    }

                    bundleDTO.setEnrollmentDate(primaryEnrollment.getAssignedAt());
                    bundleDTO.setDeadline(primaryEnrollment.getDeadline());

                    // Get courses in this bundle and calculate progress
                    List<EnrolledCoursesOutDTO> bundleCourses = processBundleCourses(bundleId, primaryEnrollment.getUserId());
                    bundleDTO.setEnrolledCoursesList(bundleCourses);

                    // Calculate bundle progress
                    float bundleProgress = bundleCourses.isEmpty() ? 0.0f :
                            (float) bundleCourses.stream()
                                    .mapToDouble(EnrolledCoursesOutDTO::getProgress)
                                    .average()
                                    .orElse(0.0);
                    bundleDTO.setProgress(bundleProgress);

                    return bundleDTO;
                })
                .collect(Collectors.toList());
    }

    private List<EnrolledCoursesOutDTO> processBundleCourses(Long bundleId, Long userId) {
        try {
            // Get course IDs for this bundle
            ResponseEntity<List<Long>> courseIdsResponse = courseMicroserviceClient.findCourseIdsByBundleId(bundleId);
            List<Long> courseIds = courseIdsResponse.getBody();

            if (courseIds == null || courseIds.isEmpty()) {
                return new ArrayList<>();
            }

            return courseIds.stream().map(courseId -> {
                EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
                courseDTO.setCourseId(courseId);

                // Get course name
                try {
                    ResponseEntity<String> courseNameResponse = courseMicroserviceClient.getCourseNameById(courseId);
                    courseDTO.setCourseName(courseNameResponse.getBody());
                } catch (Exception e) {
                    courseDTO.setCourseName("Unknown Course");
                }

                // Get course progress
                try {
                    CourseProgressWithMetaDTO progressData = courseMicroserviceClient.getCourseProgressWithMeta(
                            userId,
                            courseId
                    );
                    courseDTO.setProgress((float) progressData.getCourseCompletionPercentage());
                } catch (Exception e) {
                    courseDTO.setProgress(0.0f);
                }

                // For bundle courses, we don't set enrollment date and deadline at course level
                courseDTO.setEnrollmentDate(null);
                courseDTO.setDeadline(null);

                return courseDTO;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private long calculateTotalCourses(List<EnrolledCoursesOutDTO> enrolledCourses,
                                       List<EnrolledBundlesOutDTO> enrolledBundles) {
        long directCourses = enrolledCourses.size();
        long bundleCourses = enrolledBundles.stream()
                .mapToLong(bundle -> bundle.getEnrolledCoursesList().size())
                .sum();
        return directCourses + bundleCourses;
    }

    private float calculateAverageCompletion(List<EnrolledCoursesOutDTO> enrolledCourses,
                                             List<EnrolledBundlesOutDTO> enrolledBundles) {
        List<Float> allProgresses = new ArrayList<>();

        // Add direct course progresses
        enrolledCourses.forEach(course -> allProgresses.add(course.getProgress()));

        // Add bundle course progresses
        enrolledBundles.forEach(bundle ->
                bundle.getEnrolledCoursesList().forEach(course ->
                        allProgresses.add(course.getProgress())));

        return allProgresses.isEmpty() ? 0.0f :
                (float) allProgresses.stream()
                        .mapToDouble(Float::doubleValue)
                        .average()
                        .orElse(0.0);
    }

    private int calculateUpcomingDeadlines(List<Enrollment> enrollments) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);

        return (int) enrollments.stream()
                .filter(e -> e.getDeadline() != null)
                .filter(e -> e.getDeadline().isAfter(now) && e.getDeadline().isBefore(nextWeek))
                .count();
    }

    private boolean determineUserStatus(List<Enrollment> enrollments) {
        // User is considered active if they have at least one active enrollment
        return enrollments.stream().anyMatch(Enrollment::getIsActive);
    }

    @Override
    public List<UserEnrollmentsOutDTO> getAllUsersEnrollments() {
        // Get all active enrollments grouped by user
        List<Enrollment> allEnrollments = enrollmentRepository.findByIsActiveTrue();

        if (allEnrollments.isEmpty()) {
            return new ArrayList<>();
        }

        // Group enrollments by user ID
        Map<Long, List<Enrollment>> enrollmentsByUser = allEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getUserId));

        // Get all unique user IDs
        Set<Long> userIds = enrollmentsByUser.keySet();

        // Fetch all users at once
        List<User> users = userRepository.findAllById(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        // Get all unique course IDs and bundle IDs for batch processing
        Set<Long> allCourseIds = allEnrollments.stream()
                .filter(e -> e.getCourseId() != null)
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        Set<Long> allBundleIds = allEnrollments.stream()
                .filter(e -> e.getBundleId() != null)
                .map(Enrollment::getBundleId)
                .collect(Collectors.toSet());

        // Fetch course names in batch
        Map<Long, String> courseNamesMap = fetchCourseNamesBatch(new ArrayList<>(allCourseIds));

        // Fetch bundle names in batch
        Map<Long, String> bundleNamesMap = fetchBundleNamesBatch(new ArrayList<>(allBundleIds));

        // Fetch bundle course mappings
        Map<Long, List<Long>> bundleCourseMappings = fetchBundleCourseMappings(new ArrayList<>(allBundleIds));

        // Process each user's enrollments
        return enrollmentsByUser.entrySet().stream()
                .map(entry -> processUserEnrollmentsOptimized(
                        entry.getKey(),
                        entry.getValue(),
                        userMap,
                        courseNamesMap,
                        bundleNamesMap,
                        bundleCourseMappings))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private UserEnrollmentsOutDTO processUserEnrollmentsOptimized(
            Long userId,
            List<Enrollment> userEnrollments,
            Map<Long, User> userMap,
            Map<Long, String> courseNamesMap,
            Map<Long, String> bundleNamesMap,
            Map<Long, List<Long>> bundleCourseMappings) {

        User user = userMap.get(userId);
        if (user == null) {
            return null;
        }

        // Separate course and bundle enrollments
        List<Enrollment> courseEnrollments = userEnrollments.stream()
                .filter(e -> e.getCourseId() != null && e.getBundleId() == null)
                .collect(Collectors.toList());

        List<Enrollment> bundleEnrollments = userEnrollments.stream()
                .filter(e -> e.getBundleId() != null)
                .collect(Collectors.toList());

        // Process course enrollments with cached data
        List<EnrolledCoursesOutDTO> enrolledCoursesList = processCourseEnrollmentsOptimized(
                courseEnrollments, courseNamesMap, userId);

        // Process bundle enrollments with cached data
        List<EnrolledBundlesOutDTO> enrolledBundlesList = processBundleEnrollmentsOptimized(
                bundleEnrollments, bundleNamesMap, bundleCourseMappings, courseNamesMap, userId);

        // Calculate statistics
        long totalCourses = calculateTotalCourses(enrolledCoursesList, enrolledBundlesList);
        float averageCompletion = calculateAverageCompletion(enrolledCoursesList, enrolledBundlesList);
        int upcomingDeadlines = calculateUpcomingDeadlines(userEnrollments);
        boolean status = determineUserStatus(userEnrollments);

        // Build and return DTO
        UserEnrollmentsOutDTO result = new UserEnrollmentsOutDTO();
        result.setUserId(userId);
        result.setUserName(user.getFirstName() + " " + user.getLastName());
        result.setCourseEnrollments((long) courseEnrollments.size());
        result.setBundleEnrollments((long) bundleEnrollments.size());
        result.setTotalCourses(totalCourses);
        result.setAverageCompletion(averageCompletion);
        result.setUpcomingDeadlines(upcomingDeadlines);
        result.setStatus(status);
        result.setEnrolledCoursesList(enrolledCoursesList);
        result.setEnrolledBundlesList(enrolledBundlesList);

        return result;
    }

    private List<EnrolledCoursesOutDTO> processCourseEnrollmentsOptimized(
            List<Enrollment> courseEnrollments,
            Map<Long, String> courseNamesMap,
            Long userId) {

        return courseEnrollments.stream().map(enrollment -> {
            EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
            courseDTO.setCourseId(enrollment.getCourseId());
            courseDTO.setCourseName(courseNamesMap.getOrDefault(enrollment.getCourseId(), "Unknown Course"));

            // Get course progress
            try {
                CourseProgressWithMetaDTO progressData = courseMicroserviceClient.getCourseProgressWithMeta(
                        userId,
                        enrollment.getCourseId()
                );
                courseDTO.setProgress((float) progressData.getCourseCompletionPercentage());
            } catch (Exception e) {
                courseDTO.setProgress(0.0f);
            }

            courseDTO.setEnrollmentDate(enrollment.getAssignedAt());
            courseDTO.setDeadline(enrollment.getDeadline());

            return courseDTO;
        }).collect(Collectors.toList());
    }

    private List<EnrolledBundlesOutDTO> processBundleEnrollmentsOptimized(
            List<Enrollment> bundleEnrollments,
            Map<Long, String> bundleNamesMap,
            Map<Long, List<Long>> bundleCourseMappings,
            Map<Long, String> courseNamesMap,
            Long userId) {

        return bundleEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getBundleId))
                .entrySet().stream()
                .map(entry -> {
                    Long bundleId = entry.getKey();
                    List<Enrollment> bundleEnrollmentsList = entry.getValue();
                    Enrollment primaryEnrollment = bundleEnrollmentsList.get(0);

                    EnrolledBundlesOutDTO bundleDTO = new EnrolledBundlesOutDTO();
                    bundleDTO.setBundleId(bundleId);
                    bundleDTO.setBundleName(bundleNamesMap.getOrDefault(bundleId, "Unknown Bundle"));
                    bundleDTO.setEnrollmentDate(primaryEnrollment.getAssignedAt());
                    bundleDTO.setDeadline(primaryEnrollment.getDeadline());

                    // Get courses in this bundle using cached data
                    List<EnrolledCoursesOutDTO> bundleCourses = processBundleCoursesOptimized(
                            bundleId, userId, bundleCourseMappings, courseNamesMap);
                    bundleDTO.setEnrolledCoursesList(bundleCourses);

                    // Calculate bundle progress
                    float bundleProgress = bundleCourses.isEmpty() ? 0.0f :
                            (float) bundleCourses.stream()
                                    .mapToDouble(EnrolledCoursesOutDTO::getProgress)
                                    .average()
                                    .orElse(0.0);
                    bundleDTO.setProgress(bundleProgress);

                    return bundleDTO;
                })
                .collect(Collectors.toList());
    }

    private List<EnrolledCoursesOutDTO> processBundleCoursesOptimized(
            Long bundleId,
            Long userId,
            Map<Long, List<Long>> bundleCourseMappings,
            Map<Long, String> courseNamesMap) {

        List<Long> courseIds = bundleCourseMappings.getOrDefault(bundleId, new ArrayList<>());

        return courseIds.stream().map(courseId -> {
            EnrolledCoursesOutDTO courseDTO = new EnrolledCoursesOutDTO();
            courseDTO.setCourseId(courseId);
            courseDTO.setCourseName(courseNamesMap.getOrDefault(courseId, "Unknown Course"));

            // Get course progress
            try {
                CourseProgressWithMetaDTO progressData = courseMicroserviceClient.getCourseProgressWithMeta(
                        userId,
                        courseId
                );
                courseDTO.setProgress((float) progressData.getCourseCompletionPercentage());
            } catch (Exception e) {
                courseDTO.setProgress(0.0f);
            }

            courseDTO.setEnrollmentDate(null);
            courseDTO.setDeadline(null);

            return courseDTO;
        }).collect(Collectors.toList());
    }

    private Map<Long, String> fetchCourseNamesBatch(List<Long> courseIds) {
        Map<Long, String> courseNamesMap = new HashMap<>();

        if (courseIds.isEmpty()) {
            return courseNamesMap;
        }

        try {
            // Filter existing course IDs first
            ResponseEntity<List<Long>> existingIdsResponse = courseMicroserviceClient.getExistingCourseIds(courseIds);
            List<Long> existingCourseIds = existingIdsResponse.getBody();

            if (existingCourseIds != null) {
                for (Long courseId : existingCourseIds) {
                    try {
                        ResponseEntity<String> nameResponse = courseMicroserviceClient.getCourseNameById(courseId);
                        courseNamesMap.put(courseId, nameResponse.getBody());
                    } catch (Exception e) {
                        courseNamesMap.put(courseId, "Unknown Course");
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to individual calls if batch fails
            for (Long courseId : courseIds) {
                try {
                    ResponseEntity<String> nameResponse = courseMicroserviceClient.getCourseNameById(courseId);
                    courseNamesMap.put(courseId, nameResponse.getBody());
                } catch (Exception ex) {
                    courseNamesMap.put(courseId, "Unknown Course");
                }
            }
        }

        return courseNamesMap;
    }

    private Map<Long, String> fetchBundleNamesBatch(List<Long> bundleIds) {
        Map<Long, String> bundleNamesMap = new HashMap<>();

        if (bundleIds.isEmpty()) {
            return bundleNamesMap;
        }

        try {
            // Filter existing bundle IDs first
            ResponseEntity<List<Long>> existingIdsResponse = courseMicroserviceClient.getExistingBundleIds(bundleIds);
            List<Long> existingBundleIds = existingIdsResponse.getBody();

            if (existingBundleIds != null) {
                for (Long bundleId : existingBundleIds) {
                    try {
                        ResponseEntity<StandardResponseOutDTO<String>> nameResponse =
                                courseMicroserviceClient.getBundleNameById(bundleId);
                        bundleNamesMap.put(bundleId, nameResponse.getBody().getData());
                    } catch (Exception e) {
                        bundleNamesMap.put(bundleId, "Unknown Bundle");
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to individual calls if batch fails
            for (Long bundleId : bundleIds) {
                try {
                    ResponseEntity<StandardResponseOutDTO<String>> nameResponse =
                            courseMicroserviceClient.getBundleNameById(bundleId);
                    bundleNamesMap.put(bundleId, nameResponse.getBody().getData());
                } catch (Exception ex) {
                    bundleNamesMap.put(bundleId, "Unknown Bundle");
                }
            }
        }

        return bundleNamesMap;
    }

    private Map<Long, List<Long>> fetchBundleCourseMappings(List<Long> bundleIds) {
        Map<Long, List<Long>> bundleCourseMappings = new HashMap<>();

        for (Long bundleId : bundleIds) {
            try {
                ResponseEntity<List<Long>> courseIdsResponse = courseMicroserviceClient.findCourseIdsByBundleId(bundleId);
                bundleCourseMappings.put(bundleId, courseIdsResponse.getBody() != null ?
                        courseIdsResponse.getBody() : new ArrayList<>());
            } catch (Exception e) {
                bundleCourseMappings.put(bundleId, new ArrayList<>());
            }
        }

        return bundleCourseMappings;
    }

    @Override
    public List<UserCourseEnrollmentOutDTO> getIndividualCourseEnrollments() {
        try {
            // Get all individual course enrollments (excluding bundle enrollments)
            List<Enrollment> individualCourseEnrollments = enrollmentRepository
                    .findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull(ENROLLMENT_SOURCE_INDIVIDUAL);

            if (individualCourseEnrollments.isEmpty()) {
                return new ArrayList<>();
            }

            // Group by course ID
            Map<Long, List<Enrollment>> enrollmentsByCourse = individualCourseEnrollments.stream()
                    .collect(Collectors.groupingBy(Enrollment::getCourseId));

            // Get unique course IDs
            Set<Long> courseIds = enrollmentsByCourse.keySet();

            // Fetch course information in batch
            Map<Long, String> courseNamesMap = fetchCourseNamesBatch(new ArrayList<>(courseIds));
            Map<Long, CourseInfoOutDTO> courseInfoMap = fetchCourseInfoBatch(new ArrayList<>(courseIds));

            // Get all unique user IDs for batch processing
            Set<Long> allUserIds = individualCourseEnrollments.stream()
                    .map(Enrollment::getUserId)
                    .collect(Collectors.toSet());
            allUserIds.addAll(individualCourseEnrollments.stream()
                    .map(Enrollment::getAssignedBy)
                    .collect(Collectors.toSet()));

            // Fetch user information in batch
            Map<Long, User> userMap = fetchUsersBatch(new ArrayList<>(allUserIds));

            // Process each course
            return enrollmentsByCourse.entrySet().stream()
                    .map(entry -> {
                        Long courseId = entry.getKey();
                        List<Enrollment> courseEnrollments = entry.getValue();

                        UserCourseEnrollmentOutDTO courseDTO = new UserCourseEnrollmentOutDTO();
                        courseDTO.setCourseId(courseId);
                        courseDTO.setCourseName(courseNamesMap.getOrDefault(courseId, "Unknown Course"));
                        courseDTO.setIndividualEnrollments((long) courseEnrollments.size());

                        // Set owner information from course info
                        CourseInfoOutDTO courseInfo = courseInfoMap.get(courseId);
                        if (courseInfo != null) {
                            courseDTO.setOwnerId(courseInfo.getOwnerId());
                            courseDTO.setActive(courseInfo.isActive());
                            User owner = userMap.get(courseInfo.getOwnerId());
                            courseDTO.setOwnerName(owner != null ? owner.getUserName() : "Unknown Owner");
                        } else {
                            courseDTO.setOwnerId(null);
                            courseDTO.setActive(true);
                            courseDTO.setOwnerName("Unknown Owner");
                        }

                        // Process enrolled users
                        List<EnrolledUserOutDTO> enrolledUsers = courseEnrollments.stream()
                                .map(enrollment -> {
                                    EnrolledUserOutDTO userDTO = new EnrolledUserOutDTO();
                                    userDTO.setUserId(enrollment.getUserId());
                                    userDTO.setEnrollmentDate(enrollment.getAssignedAt());
                                    userDTO.setDeadline(enrollment.getDeadline());

                                    // Set user name
                                    User user = userMap.get(enrollment.getUserId());
                                    userDTO.setUserName(user != null ? user.getUserName() : "Unknown User");

                                    // Set assigned by name
                                    User assignedByUser = userMap.get(enrollment.getAssignedBy());
                                    userDTO.setAssignedByName(assignedByUser != null ? assignedByUser.getUserName() : "Unknown");

                                    // Get progress
                                    try {
                                        CourseProgressWithMetaDTO progressData = courseMicroserviceClient
                                                .getCourseProgressWithMeta(
                                                        enrollment.getUserId(),
                                                        courseId
                                                );
                                        userDTO.setProgress(progressData.getCourseCompletionPercentage());
                                    } catch (Exception e) {
                                        userDTO.setProgress(0.0);
                                    }

                                    return userDTO;
                                })
                                .collect(Collectors.toList());

                        courseDTO.setEnrolledUserOutDTOList(enrolledUsers);
                        return courseDTO;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResourceNotValidException("Failed to fetch individual course enrollments: " + e.getMessage());
        }
    }

    @Override
    public List<UserBundleEnrollmentOutDTO> getIndividualBundleEnrollments() {
        try {
            // Get all individual bundle enrollments
            List<Enrollment> individualBundleEnrollments = enrollmentRepository
                    .findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull(ENROLLMENT_SOURCE_BUNDLE);

            if (individualBundleEnrollments.isEmpty()) {
                return new ArrayList<>();
            }

            // Group by bundle ID
            Map<Long, List<Enrollment>> enrollmentsByBundle = individualBundleEnrollments.stream()
                    .collect(Collectors.groupingBy(Enrollment::getBundleId));

            // Get unique bundle IDs
            Set<Long> bundleIds = enrollmentsByBundle.keySet();

            // Fetch bundle information in batch
            Map<Long, String> bundleNamesMap = fetchBundleNamesBatch(new ArrayList<>(bundleIds));
            Map<Long, BundleInfoOutDTO> bundleInfoMap = fetchBundleInfoBatch(new ArrayList<>(bundleIds));
            Map<Long, List<Long>> bundleCourseMappings = fetchBundleCourseMappings(new ArrayList<>(bundleIds));

            // Get all unique user IDs for batch processing
            Set<Long> allUserIds = individualBundleEnrollments.stream()
                    .map(Enrollment::getUserId)
                    .collect(Collectors.toSet());
            allUserIds.addAll(individualBundleEnrollments.stream()
                    .map(Enrollment::getAssignedBy)
                    .collect(Collectors.toSet()));

            // Fetch user information in batch
            Map<Long, User> userMap = fetchUsersBatch(new ArrayList<>(allUserIds));

            // Process each bundle
            return enrollmentsByBundle.entrySet().stream()
                    .map(entry -> {
                        Long bundleId = entry.getKey();
                        List<Enrollment> bundleEnrollments = entry.getValue();

                        UserBundleEnrollmentOutDTO bundleDTO = new UserBundleEnrollmentOutDTO();
                        bundleDTO.setBundleId(bundleId);
                        bundleDTO.setBundleName(bundleNamesMap.getOrDefault(bundleId, "Unknown Bundle"));

                        // Set bundle information
                        BundleInfoOutDTO bundleInfo = bundleInfoMap.get(bundleId);
                        if (bundleInfo != null) {
                            bundleDTO.setTotalCourses(bundleInfo.getTotalCourses());
                            bundleDTO.setActive(bundleInfo.isActive());
                        } else {
                            List<Long> courseIds = bundleCourseMappings.getOrDefault(bundleId, new ArrayList<>());
                            bundleDTO.setTotalCourses((long) courseIds.size());
                            bundleDTO.setActive(true);
                        }

                        // Get unique users enrolled in this bundle
                        Map<Long, List<Enrollment>> enrollmentsByUser = bundleEnrollments.stream()
                                .collect(Collectors.groupingBy(Enrollment::getUserId));

                        bundleDTO.setIndividualEnrollments((long) enrollmentsByUser.size());

                        // Calculate average completion across all users and courses in bundle
                        List<Double> allProgressValues = new ArrayList<>();

                        // Process enrolled users
                        List<EnrolledUserOutDTO> enrolledUsers = enrollmentsByUser.entrySet().stream()
                                .map(userEntry -> {
                                    Long userId = userEntry.getKey();
                                    List<Enrollment> userBundleEnrollments = userEntry.getValue();

                                    // Use the first enrollment for basic info (they should all have same assignment details)
                                    Enrollment primaryEnrollment = userBundleEnrollments.get(0);

                                    EnrolledUserOutDTO userDTO = new EnrolledUserOutDTO();
                                    userDTO.setUserId(userId);
                                    userDTO.setEnrollmentDate(primaryEnrollment.getAssignedAt());
                                    userDTO.setDeadline(primaryEnrollment.getDeadline());

                                    // Set user name
                                    User user = userMap.get(userId);
                                    userDTO.setUserName(user != null ? user.getUserName() : "Unknown User");

                                    // Set assigned by name
                                    User assignedByUser = userMap.get(primaryEnrollment.getAssignedBy());
                                    userDTO.setAssignedByName(assignedByUser != null ? assignedByUser.getUserName() : "Unknown");

                                    // Calculate average progress for this user across all courses in bundle
                                    List<Double> userProgressValues = userBundleEnrollments.stream()
                                            .map(enrollment -> {
                                                try {
                                                    CourseProgressWithMetaDTO progressData = courseMicroserviceClient
                                                            .getCourseProgressWithMeta(
                                                                    userId,
                                                                    enrollment.getCourseId()
                                                            );
                                                    return progressData.getCourseCompletionPercentage();
                                                } catch (Exception e) {
                                                    return 0.0;
                                                }
                                            })
                                            .toList();

                                    double userAverageProgress = userProgressValues.stream()
                                            .mapToDouble(Double::doubleValue)
                                            .average()
                                            .orElse(0.0);

                                    userDTO.setProgress(userAverageProgress);

                                    // Add to overall progress calculation
                                    allProgressValues.addAll(userProgressValues);

                                    return userDTO;
                                })
                                .collect(Collectors.toList());

                        bundleDTO.setEnrolledUserOutDTOList(enrolledUsers);

                        // Calculate overall average completion for the bundle
                        float averageCompletion = allProgressValues.isEmpty() ? 0.0f :
                                (float) allProgressValues.stream()
                                        .mapToDouble(Double::doubleValue)
                                        .average()
                                        .orElse(0.0);

                        bundleDTO.setAverageCompletion(averageCompletion);

                        return bundleDTO;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResourceNotValidException("Failed to fetch individual bundle enrollments: " + e.getMessage());
        }
    }

// Helper methods for batch processing

    private Map<Long, CourseInfoOutDTO> fetchCourseInfoBatch(List<Long> courseIds) {
        Map<Long, CourseInfoOutDTO> courseInfoMap = new HashMap<>();

        try {
            ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response =
                    courseMicroserviceClient.getCourseInfo();

            if (response.getBody() != null && response.getBody().getData() != null) {
                List<CourseInfoOutDTO> allCourses = response.getBody().getData();
                courseInfoMap = allCourses.stream()
                        .filter(course -> courseIds.contains(course.getCourseId()))
                        .collect(Collectors.toMap(CourseInfoOutDTO::getCourseId, Function.identity()));
            }
        } catch (Exception e) {
            // If batch fetch fails, return empty map
        }

        return courseInfoMap;
    }

    private Map<Long, BundleInfoOutDTO> fetchBundleInfoBatch(List<Long> bundleIds) {
        Map<Long, BundleInfoOutDTO> bundleInfoMap = new HashMap<>();

        try {
            ResponseEntity<StandardResponseOutDTO<List<BundleInfoOutDTO>>> response =
                    courseMicroserviceClient.getBundleInfo();

            if (response.getBody() != null && response.getBody().getData() != null) {
                List<BundleInfoOutDTO> allBundles = response.getBody().getData();
                bundleInfoMap = allBundles.stream()
                        .filter(bundle -> bundleIds.contains(bundle.getBundleId()))
                        .collect(Collectors.toMap(BundleInfoOutDTO::getBundleId, Function.identity()));
            }
        } catch (Exception e) {
            // If batch fetch fails, return empty map
        }

        return bundleInfoMap;
    }

    private Map<Long, User> fetchUsersBatch(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<User> users = userRepository.findAllById(userIds);
            return users.stream()
                    .collect(Collectors.toMap(User::getUserId, Function.identity()));
        } catch (Exception e) {
            return new HashMap<>();
        }
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

    // Add this method to your EnrollmentServiceImpl class
    private boolean enrollmentExists(Long userId, Long groupId, Long courseId, Long bundleId, String enrollmentSource) {
        return enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                userId, groupId, courseId, bundleId, enrollmentSource, true);
    }

    // Updated enrollUsersToCourses method
    private List<Enrollment> enrollUsersToCourses(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long courseId : requestDTO.getCourseIds()) {
                // Check if enrollment already exists for this user-course combination with INDIVIDUAL source
                if (enrollmentExists(userId, null, courseId, null, ENROLLMENT_SOURCE_INDIVIDUAL)) {
                    throw new ResourceAlreadyExistsException(
                            "User with ID " + userId + " is already individually enrolled in course with ID " + courseId);
                }

                Enrollment enrollment = createEnrollment(
                        userId, null, courseId, null,
                        ENROLLMENT_SOURCE_INDIVIDUAL, requestDTO
                );
                enrollments.add(enrollment);
            }
        }

        return enrollments;
    }

    // Updated enrollUsersToBundles method
    private List<Enrollment> enrollUsersToBundles(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long userId : requestDTO.getUserIds()) {
            for (Long bundleId : requestDTO.getBundleIds()) {
                // Get all courses in the bundle
                List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();

                if (courseIds != null && !courseIds.isEmpty()) {
                    // Check if any course in the bundle is already enrolled for this user with BUNDLE source
                    for (Long courseId : courseIds) {
                        if (enrollmentExists(userId, null, courseId, bundleId, ENROLLMENT_SOURCE_BUNDLE)) {
                            throw new ResourceAlreadyExistsException(
                                    "User with ID " + userId + " is already enrolled in bundle with ID " + bundleId +
                                            " (course " + courseId + " already exists)");
                        }
                    }

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
        }

        return enrollments;
    }

    // Updated enrollGroupsToCourses method
    private List<Enrollment> enrollGroupsToCourses(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            // Get all users in the group
            List<Long> userIds = userGroupRepository.findUserIdsByGroupId(groupId);

            for (Long courseId : requestDTO.getCourseIds()) {
                // Check if any user in the group is already enrolled in this course through this group
                for (Long userId : userIds) {
                    if (enrollmentExists(userId, groupId, courseId, null, ENROLLMENT_SOURCE_GROUP)) {
                        throw new ResourceAlreadyExistsException(
                                "Group with ID " + groupId + " is already enrolled in course with ID " + courseId +
                                        " (user " + userId + " already enrolled through this group)");
                    }
                }

                // Create enrollment for each user in the group
                for (Long userId : userIds) {
                    Enrollment enrollment = createEnrollment(
                            userId, groupId, courseId, null,
                            ENROLLMENT_SOURCE_GROUP, requestDTO
                    );
                    enrollments.add(enrollment);
                }
            }
        }

        return enrollments;
    }

    // Updated enrollGroupsToBundles method
    private List<Enrollment> enrollGroupsToBundles(EnrollmentRequestInDTO requestDTO) {
        List<Enrollment> enrollments = new ArrayList<>();

        for (Long groupId : requestDTO.getGroupIds()) {
            // Get all users in the group
            List<Long> userIds = userGroupRepository.findUserIdsByGroupId(groupId);

            for (Long bundleId : requestDTO.getBundleIds()) {
                // Get all courses in the bundle
                List<Long> courseIds = courseMicroserviceClient.findCourseIdsByBundleId(bundleId).getBody();

                if (courseIds != null && !courseIds.isEmpty()) {
                    // Check if any user-course combination already exists for this group-bundle
                    for (Long userId : userIds) {
                        for (Long courseId : courseIds) {
                            if (enrollmentExists(userId, groupId, courseId, bundleId, ENROLLMENT_SOURCE_GROUP_BUNDLE)) {
                                throw new ResourceAlreadyExistsException(
                                        "Group with ID " + groupId + " is already enrolled in bundle with ID " + bundleId +
                                                " (user " + userId + " already enrolled in course " + courseId + " through this group-bundle combination)");
                            }
                        }
                    }

                    // Create enrollment for each user-course combination
                    for (Long userId : userIds) {
                        for (Long courseId : courseIds) {
                            Enrollment enrollment = createEnrollment(
                                    userId, groupId, courseId, bundleId,
                                    ENROLLMENT_SOURCE_GROUP_BUNDLE, requestDTO
                            );
                            enrollments.add(enrollment);
                        }
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

    // Updated conversion method (removed parentEnrollmentId and progressPercentage)
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
}
