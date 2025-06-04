package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.EnrollmentConvertor;
import com.nt.LMS.dto.*;
import com.nt.LMS.entities.*;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.*;
import com.nt.LMS.service.EnrollmentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseMicroserviceClient courseMicroserviceClient;
    private final UserCourseEnrollmentRepository userCourseEnrollmentRepository;
    private final UserBundleEnrollmentRepository userBundleEnrollmentRepository;
    private final GroupCourseEnrollmentRepository groupCourseEnrollmentRepository;
    private final GroupBundleEnrollmentRepository groupBundleEnrollmentRepository;
    private final EnrollmentHistoryRepository enrollmentHistoryRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final EnrollmentConvertor enrollmentConvertor;

    private final Executor asyncExecutor = Executors.newFixedThreadPool(10);
    private static final List<String> INACTIVE_STATUSES = Arrays.asList("COMPLETED", "EXPIRED", "UNENROLLED");
    private static final int UPCOMING_DEADLINE_DAYS = 7;

    @Transactional
    @Override
    public String enrollUser(EnrollmentDTO enrollmentDTO) {
        validateEnrollmentRequest(enrollmentDTO);

        User manager = getManagerById(enrollmentDTO.getAssignedBy());
        LocalDateTime now = LocalDateTime.now();

        if (enrollmentDTO.getUserId() != null) {
            return processUserEnrollment(enrollmentDTO, now, manager);
        } else if (enrollmentDTO.getGroupId() != null) {
            return processGroupEnrollment(enrollmentDTO, now, manager);
        } else {
            throw new InvalidRequestException("Either userId or groupId must be provided");
        }
    }

    private void validateEnrollmentRequest(EnrollmentDTO enrollmentDTO) {
        if (enrollmentDTO.getAssignedBy() == null) {
            throw new InvalidRequestException("AssignedBy is required");
        }
        if (enrollmentDTO.getCourseId() == null && enrollmentDTO.getBundleId() == null) {
            throw new InvalidRequestException("Either courseId or bundleId must be provided");
        }
        if (enrollmentDTO.getCourseId() != null && enrollmentDTO.getBundleId() != null) {
            throw new InvalidRequestException("Cannot assign both course and bundle simultaneously");
        }
    }

    private User getManagerById(Long managerId) {
        return userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + managerId));
    }

    private String processUserEnrollment(EnrollmentDTO enrollmentDTO, LocalDateTime now, User manager) {
        User user = validateUserManagerRelationship(enrollmentDTO.getUserId(), manager.getUserId());

        if (enrollmentDTO.getCourseId() != null) {
            enrollUserInCourse(enrollmentDTO, now);
        } else {
            enrollUserInBundle(enrollmentDTO, now);
        }

        return "User enrollment completed successfully";
    }

    private User validateUserManagerRelationship(Long userId, Long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!Objects.equals(user.getManagerId(), managerId)) {
            throw new InvalidRequestException("You cannot assign courses to this user - not under your management");
        }

        return user;
    }

    private void enrollUserInCourse(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        validateCourseExists(enrollmentDTO.getCourseId());
        checkExistingUserCourseEnrollment(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId());

        UserCourseEnrollment enrollment = enrollmentConvertor.toUserCourseEnrollment(enrollmentDTO, now);
        userCourseEnrollmentRepository.save(enrollment);

        createOrUpdateEnrollment(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId(), enrollmentDTO.getAssignedBy());

        logEnrollmentHistoryAsync(
                enrollmentDTO.getUserId(), null, enrollmentDTO.getCourseId(), null,
                enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
        );
    }

    private void enrollUserInBundle(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        validateBundleExists(enrollmentDTO.getBundleId());
        checkExistingUserBundleEnrollment(enrollmentDTO.getUserId(), enrollmentDTO.getBundleId());

        UserBundleEnrollment enrollment = enrollmentConvertor.toUserBundleEnrollment(enrollmentDTO, now);
        userBundleEnrollmentRepository.save(enrollment);

        logEnrollmentHistoryAsync(
                enrollmentDTO.getUserId(), null, null, enrollmentDTO.getBundleId(),
                enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
        );

        processCoursesInBundle(enrollmentDTO, now);
    }

    private void processCoursesInBundle(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        List<CourseBundleDTO> courseBundles = getCoursesInBundle(enrollmentDTO.getBundleId());

        List<CompletableFuture<Void>> futures = courseBundles.stream()
                .map(courseBundle -> CompletableFuture.runAsync(() -> {
                    createOrUpdateEnrollment(enrollmentDTO.getUserId(), courseBundle.getCourseId(), enrollmentDTO.getAssignedBy());
                    logEnrollmentHistoryAsync(
                            enrollmentDTO.getUserId(), null, courseBundle.getCourseId(), enrollmentDTO.getBundleId(),
                            enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
                    );
                }, asyncExecutor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private String processGroupEnrollment(EnrollmentDTO enrollmentDTO, LocalDateTime now, User manager) {
        Group group = getGroupById(enrollmentDTO.getGroupId());
        List<UserGroup> userGroups = getUserGroupsWithValidation(group.getGroupId(), manager.getUserId());

        if (enrollmentDTO.getCourseId() != null) {
            enrollGroupInCourse(enrollmentDTO, userGroups, now);
        } else {
            enrollGroupInBundle(enrollmentDTO, userGroups, now);
        }

        return "Group enrollment completed successfully";
    }

    private Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
    }

    private List<UserGroup> getUserGroupsWithValidation(Long groupId, Long managerId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupId(groupId);
        if (CollectionUtils.isEmpty(userGroups)) {
            throw new ResourceNotFoundException("No active users found in the group");
        }

        validateGroupManagerAuthority(userGroups, managerId);
        return userGroups;
    }

    private void validateGroupManagerAuthority(List<UserGroup> userGroups, Long managerId) {
        List<Long> userIds = userGroups.stream()
                .map(UserGroup::getUserId)
                .collect(Collectors.toList());

        Map<Long, User> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        boolean hasUnauthorizedUsers = userGroups.stream()
                .anyMatch(userGroup -> {
                    User user = users.get(userGroup.getUserId());
                    return user == null || !Objects.equals(user.getManagerId(), managerId);
                });

        if (hasUnauthorizedUsers) {
            throw new ResourceConflictException("Group contains users not under your management");
        }
    }

    private void enrollGroupInCourse(EnrollmentDTO enrollmentDTO, List<UserGroup> userGroups, LocalDateTime now) {
        validateCourseExists(enrollmentDTO.getCourseId());
        checkExistingGroupCourseEnrollment(enrollmentDTO.getGroupId(), enrollmentDTO.getCourseId());

        GroupCourseEnrollment groupEnrollment = enrollmentConvertor.toGroupCourseEnrollment(enrollmentDTO, now);
        groupCourseEnrollmentRepository.save(groupEnrollment);

        logEnrollmentHistoryAsync(
                null, enrollmentDTO.getGroupId(), enrollmentDTO.getCourseId(), null,
                enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
        );

        processIndividualUserEnrollments(userGroups, enrollmentDTO, now);
    }

    private void enrollGroupInBundle(EnrollmentDTO enrollmentDTO, List<UserGroup> userGroups, LocalDateTime now) {
        validateBundleExists(enrollmentDTO.getBundleId());
        checkExistingGroupBundleEnrollment(enrollmentDTO.getGroupId(), enrollmentDTO.getBundleId());

        List<CourseBundleDTO> courseBundles = getCoursesInBundle(enrollmentDTO.getBundleId());

        GroupBundleEnrollment groupEnrollment = enrollmentConvertor.toGroupBundleEnrollment(enrollmentDTO, now);
        groupBundleEnrollmentRepository.save(groupEnrollment);

        logEnrollmentHistoryAsync(
                null, enrollmentDTO.getGroupId(), null, enrollmentDTO.getBundleId(),
                enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
        );

        processGroupBundleEnrollments(userGroups, courseBundles, enrollmentDTO, now);
    }

    private void processIndividualUserEnrollments(List<UserGroup> userGroups, EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        List<CompletableFuture<Void>> futures = userGroups.stream()
                .map(userGroup -> CompletableFuture.runAsync(() -> {
                    createOrUpdateEnrollment(userGroup.getUserId(), enrollmentDTO.getCourseId(), enrollmentDTO.getAssignedBy());
                    logEnrollmentHistoryAsync(
                            userGroup.getUserId(), enrollmentDTO.getGroupId(), enrollmentDTO.getCourseId(), null,
                            enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(), now, "ENROLLED"
                    );
                }, asyncExecutor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processGroupBundleEnrollments(List<UserGroup> userGroups, List<CourseBundleDTO> courseBundles,
                                               EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (UserGroup userGroup : userGroups) {
            for (CourseBundleDTO courseBundle : courseBundles) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    createOrUpdateEnrollment(userGroup.getUserId(), courseBundle.getCourseId(), enrollmentDTO.getAssignedBy());
                    logEnrollmentHistoryAsync(
                            userGroup.getUserId(), enrollmentDTO.getGroupId(), courseBundle.getCourseId(),
                            enrollmentDTO.getBundleId(), enrollmentDTO.getDeadline(), enrollmentDTO.getAssignedBy(),
                            now, "ENROLLED"
                    );
                }, asyncExecutor);
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public long countEnrollments() {
        return enrollmentRepository.count();
    }

    @Override
    public EnrollmentDashBoardStatsDTO getEnrollmentStats() {
        try {
            Long activeUserEnrollments = userCourseEnrollmentRepository.countByStatusNotIn(INACTIVE_STATUSES) +
                    userBundleEnrollmentRepository.countByStatusNotIn(INACTIVE_STATUSES);
            Long activeGroupEnrollments = groupCourseEnrollmentRepository.countByStatusNotIn(INACTIVE_STATUSES) +
                    groupBundleEnrollmentRepository.countByStatusNotIn(INACTIVE_STATUSES);

            if (activeUserEnrollments == 0 && activeGroupEnrollments == 0) {
                throw new ResourceNotFoundException("No active enrollments found");
            }

            Long totalActiveUsers = calculateTotalActiveUsers();
            Long courseCompletions = enrollmentHistoryRepository.countByStatusIn(List.of("COMPLETED"));
            Long upcomingDeadlines = calculateUpcomingDeadlines();

            EnrollmentDashBoardStatsDTO stats = new EnrollmentDashBoardStatsDTO();
            stats.setUsersEnrolled(totalActiveUsers);
            stats.setGroupsEnrolled(activeGroupEnrollments);
            stats.setTotalEnrollments(enrollmentRepository.count());
            stats.setCourseCompletions(courseCompletions);
            stats.setTopEnrolledCourse(getTopEnrolledCourseName());
            stats.setUpcomingDeadlines(upcomingDeadlines);
            stats.setCompletionRate(calculateCompletionRate(courseCompletions, activeUserEnrollments));

            return stats;
        } catch (FeignException e) {
            log.error("Error contacting course service", e);
            throw new RuntimeException("Course service unavailable");
        } catch (Exception e) {
            log.error("Error calculating enrollment stats", e);
            throw new RuntimeException("Failed to calculate enrollment statistics", e);
        }
    }

    @Override
    public List<UserEnrollmentsDTO> getEnrollmentsForUser() {
        List<User> activeUsers = getActiveUsers();
        if (CollectionUtils.isEmpty(activeUsers)) {
            throw new ResourceNotFoundException("No active users found");
        }

        return activeUsers.parallelStream()
                .map(this::buildUserEnrollmentDTO)
                .collect(Collectors.toList());
    }

    private List<User> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.isActive() && user.getUserId() != 1)
                .collect(Collectors.toList());
    }

    private UserEnrollmentsDTO buildUserEnrollmentDTO(User user) {
        Long userId = user.getUserId();
        LocalDateTime now = LocalDateTime.now();

        long courseEnrollments = userCourseEnrollmentRepository.countByUserIdAndStatusNotIn(userId, INACTIVE_STATUSES);
        long bundleEnrollments = userBundleEnrollmentRepository.countByUserIdAndStatusNotIn(userId, INACTIVE_STATUSES);

        List<EnrolledCoursesDTO> enrolledCourses = buildEnrolledCoursesList(userId, now);
        List<EnrolledBundlesDTO> enrolledBundles = buildEnrolledBundlesList(userId, now);

        int upcomingDeadlines = calculateUserUpcomingDeadlines(enrolledCourses, enrolledBundles, now);

        return enrollmentConvertor.toUserEnrollmentsDTO(
                user, enrolledCourses, enrolledBundles,
                courseEnrollments, bundleEnrollments, upcomingDeadlines
        );
    }

    private List<EnrolledCoursesDTO> buildEnrolledCoursesList(Long userId, LocalDateTime now) {
        List<UserCourseEnrollment> enrollments = userCourseEnrollmentRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(enrollments)) {
            return new ArrayList<>();
        }

        Map<Long, String> courseNames = fetchCourseNames(enrollments.stream()
                .map(UserCourseEnrollment::getCourseId)
                .collect(Collectors.toSet()));

        Map<Long, Float> progressMap = fetchProgressData(userId, enrollments.stream()
                .map(UserCourseEnrollment::getCourseId)
                .collect(Collectors.toSet()));

        return enrollmentConvertor.toEnrolledCoursesDTOList(enrollments, courseNames, progressMap);
    }

    private List<EnrolledBundlesDTO> buildEnrolledBundlesList(Long userId, LocalDateTime now) {
        List<UserBundleEnrollment> enrollments = userBundleEnrollmentRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(enrollments)) {
            return new ArrayList<>();
        }

        Map<Long, String> bundleNames = fetchBundleNames(enrollments.stream()
                .map(UserBundleEnrollment::getBundleId)
                .collect(Collectors.toSet()));

        return enrollmentConvertor.toEnrolledBundlesDTOList(enrollments, bundleNames);
    }

    private Map<Long, String> fetchCourseNames(Set<Long> courseIds) {
        return courseIds.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        courseId -> courseId,
                        enrollmentConvertor::getCourseNameSafely
                ));
    }

    private Map<Long, String> fetchBundleNames(Set<Long> bundleIds) {
        return bundleIds.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        bundleId -> bundleId,
                        enrollmentConvertor::getBundleNameSafely
                ));
    }

    private Map<Long, Float> fetchProgressData(Long userId, Set<Long> courseIds) {
        return courseIds.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        courseId -> courseId,
                        courseId -> enrollmentConvertor.getProgressSafely(userId.intValue(), courseId.intValue())
                ));
    }

    private int calculateUserUpcomingDeadlines(List<EnrolledCoursesDTO> courses,
                                               List<EnrolledBundlesDTO> bundles,
                                               LocalDateTime now) {
        int count = 0;
        LocalDateTime sevenDaysFromNow = now.plusDays(UPCOMING_DEADLINE_DAYS);

        count += courses.stream()
                .mapToInt(course -> isUpcomingDeadline(course.getDeadline(), now, sevenDaysFromNow) ? 1 : 0)
                .sum();

        count += bundles.stream()
                .mapToInt(bundle -> isUpcomingDeadline(bundle.getDeadline(), now, sevenDaysFromNow) ? 1 : 0)
                .sum();

        return count;
    }

    private boolean isUpcomingDeadline(LocalDateTime deadline, LocalDateTime now, LocalDateTime sevenDaysFromNow) {
        return deadline != null && !deadline.isBefore(now) && deadline.isBefore(sevenDaysFromNow);
    }

    // Validation helper methods
    private void validateCourseExists(Long courseId) {
        if (!courseMicroserviceClient.courseExistsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
    }

    private void validateBundleExists(Long bundleId) {
        if (!courseMicroserviceClient.bundleExistsById(bundleId)) {
            throw new ResourceNotFoundException("Bundle not found with ID: " + bundleId);
        }
    }

    private void checkExistingUserCourseEnrollment(Long userId, Long courseId) {
        userCourseEnrollmentRepository.findByUserIdAndCourseIdAndStatusNotIn(userId, courseId, INACTIVE_STATUSES)
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("User is already enrolled in this course");
                });
    }

    private void checkExistingUserBundleEnrollment(Long userId, Long bundleId) {
        userBundleEnrollmentRepository.findByUserIdAndBundleIdAndStatusNotIn(userId, bundleId, INACTIVE_STATUSES)
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("User is already enrolled in this bundle");
                });
    }

    private void checkExistingGroupCourseEnrollment(Long groupId, Long courseId) {
        groupCourseEnrollmentRepository.findByGroupIdAndCourseIdAndStatusNotIn(groupId, courseId, INACTIVE_STATUSES)
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("Group is already enrolled in this course");
                });
    }

    private void checkExistingGroupBundleEnrollment(Long groupId, Long bundleId) {
        groupBundleEnrollmentRepository.findByGroupIdAndBundleIdAndStatusNotIn(groupId, bundleId, INACTIVE_STATUSES)
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("Group is already enrolled in this bundle");
                });
    }

    // Helper methods
    private List<CourseBundleDTO> getCoursesInBundle(Long bundleId) {
        List<CourseBundleDTO> courseBundles = courseMicroserviceClient.getAllCoursesByBundleId(bundleId).getBody();
        if (CollectionUtils.isEmpty(courseBundles)) {
            throw new ResourceNotFoundException("No courses found in bundle with ID: " + bundleId);
        }
        return courseBundles;
    }

    private void createOrUpdateEnrollment(Long userId, Long courseId, Long assignedBy) {
        Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(userId, courseId);
        if (existingEnrollment == null) {
            Enrollment enrollment = enrollmentConvertor.toEnrollment(userId, courseId, assignedBy);
            enrollmentRepository.save(enrollment);
        }
    }

    private void logEnrollmentHistoryAsync(Long userId, Long groupId, Long courseId, Long bundleId,
                                           LocalDateTime deadline, Long assignedBy, LocalDateTime recordedAt,
                                           String actionType) {
        CompletableFuture.runAsync(() -> {
            try {
                EnrollmentHistory history = enrollmentConvertor.toEnrollmentHistory(
                        userId, groupId, courseId, bundleId, deadline, assignedBy, recordedAt, actionType
                );
                enrollmentHistoryRepository.save(history);
            } catch (Exception e) {
                log.error("Failed to log enrollment history", e);
            }
        }, asyncExecutor);
    }

    private Long calculateTotalActiveUsers() {
        return userCourseEnrollmentRepository.findAll().stream()
                .map(UserCourseEnrollment::getUserId)
                .distinct()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(user -> user.isActive() && user.getUserId() != 1)
                .count();
    }

    private Long calculateUpcomingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysFromNow = now.plusDays(UPCOMING_DEADLINE_DAYS);

        return userCourseEnrollmentRepository.countByDeadlineBetween(now, sevenDaysFromNow) +
                userBundleEnrollmentRepository.countByDeadlineBetween(now, sevenDaysFromNow);
    }

    private String getTopEnrolledCourseName() {
        try {
            Long popularCourseId = enrollmentRepository.findMostFrequentEnrolledCourseId();
            if (popularCourseId != null) {
                return courseMicroserviceClient.getCourseNameById(popularCourseId).getBody();
            }
            return "N/A";
        } catch (Exception e) {
            log.warn("Failed to get top enrolled course name", e);
            return "N/A";
        }
    }

    private Long calculateCompletionRate(Long completions, Long totalEnrollments) {
        if (totalEnrollments == 0) return 0L;
        return Math.round((completions.doubleValue() / totalEnrollments.doubleValue()) * 100);
    }
}