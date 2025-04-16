package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.CourseBundleDTO;
import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.*;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.*;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.UserCourseEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseMicroserviceClient courseMicroserviceClient;
    @Autowired
    UserCourseEnrollmentRepository userCourseEnrollmentRepository;
    @Autowired
    UserBundleEnrollmentRepository userBundleEnrollmentRepository;
    @Autowired
    GroupCourseEnrollmentRepository groupCourseEnrollmentRepository;
    @Autowired
    GroupBundleEnrollmentRepository groupBundleEnrollmentRepository;
    @Autowired
    EnrollmentHistoryRepository enrollmentHistoryRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserGroupRepository userGroupRepository;

    @Override
    public String enrollUser(EnrollmentDTO enrollmentDTO) {
        // Validate common prerequisites
        User manager = userRepository.findById(enrollmentDTO.getAssignedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        LocalDateTime now = LocalDateTime.now();

        if (enrollmentDTO.getUserId() != null) {
            return processUserEnrollment(enrollmentDTO, now);
        } else if (enrollmentDTO.getGroupId() != null) {
            return processGroupEnrollment(enrollmentDTO, now);
        } else {
            throw new InvalidRequestException("Either userId or groupId must be provided");
        }
    }

    private String processUserEnrollment(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        // Verify user and manager relationship
        User user = userRepository.findById(enrollmentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!Objects.equals(user.getManagerId(), enrollmentDTO.getAssignedBy())) {
            throw new InvalidRequestException("You cannot assign courses to this user");
        }

        if (enrollmentDTO.getCourseId() != null) {
            enrollUserInCourse(enrollmentDTO, now);
        } else if (enrollmentDTO.getBundleId() != null) {
            enrollUserInBundle(enrollmentDTO, now);
        } else {
            throw new InvalidRequestException("Either courseId or bundleId must be provided");
        }

        return "Enrollment Successful";
    }

    private void enrollUserInCourse(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        // Verify course exists
        if (!courseMicroserviceClient.courseExistsById(enrollmentDTO.getCourseId())) {
            throw new ResourceNotFoundException("Course Not Found");
        }

        // Check if already enrolled
        userCourseEnrollmentRepository.findByUserIdAndCourseIdAndStatusNotIn(
                        enrollmentDTO.getUserId(),
                        enrollmentDTO.getCourseId(),
                        Arrays.asList("COMPLETED", "EXPIRED"))
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("This course is already assigned to the user");
                });

        // Create enrollment records
        UserCourseEnrollment userCourseEnrollment = new UserCourseEnrollment();
        userCourseEnrollment.setUserId(enrollmentDTO.getUserId());
        userCourseEnrollment.setCourseId(enrollmentDTO.getCourseId());
        userCourseEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
        userCourseEnrollment.setStatus("ENROLLED");
        userCourseEnrollment.setAssignedAt(now);
        userCourseEnrollment.setDeadline(enrollmentDTO.getDeadline());
        userCourseEnrollmentRepository.save(userCourseEnrollment);

        // Update main enrollment record if needed
        createOrUpdateEnrollment(
                enrollmentDTO.getUserId(),
                enrollmentDTO.getCourseId(),
                enrollmentDTO.getAssignedBy()
        );

        // Log history
        logEnrollmentHistory(
                enrollmentDTO.getUserId(),
                null,
                enrollmentDTO.getCourseId(),
                null,
                enrollmentDTO.getDeadline(),
                enrollmentDTO.getAssignedBy(),
                now,
                "ASSIGNED"
        );
    }

    private void enrollUserInBundle(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        // Verify bundle exists
        if (!courseMicroserviceClient.bundleExistsById(enrollmentDTO.getBundleId())) {
            throw new ResourceNotFoundException("Bundle not found");
        }

        // Check if already enrolled
        userBundleEnrollmentRepository.findByUserIdAndBundleIdAndStatusNotIn(
                        enrollmentDTO.getUserId(),
                        enrollmentDTO.getBundleId(),
                        Arrays.asList("COMPLETED", "EXPIRED"))
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("Bundle is already assigned to the user");
                });

        // Create bundle enrollment record
        UserBundleEnrollment userBundleEnrollment = new UserBundleEnrollment();
        userBundleEnrollment.setUserId(enrollmentDTO.getUserId());
        userBundleEnrollment.setBundleId(enrollmentDTO.getBundleId());
        userBundleEnrollment.setDeadline(enrollmentDTO.getDeadline());
        userBundleEnrollment.setStatus("ENROLLED");
        userBundleEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
        userBundleEnrollment.setAssignedAt(now);
        userBundleEnrollmentRepository.save(userBundleEnrollment);

        // Log bundle enrollment
        logEnrollmentHistory(
                enrollmentDTO.getUserId(),
                null,
                null,
                enrollmentDTO.getBundleId(),
                enrollmentDTO.getDeadline(),
                enrollmentDTO.getAssignedBy(),
                now,
                "ASSIGNED"
        );

        // Process all courses in the bundle
        List<CourseBundleDTO> courseBundleDTOs = getCoursesInBundle(enrollmentDTO.getBundleId());

        for (CourseBundleDTO courseBundle : courseBundleDTOs) {
            // Update enrollment record
            createOrUpdateEnrollment(
                    enrollmentDTO.getUserId(),
                    courseBundle.getCourseId(),
                    enrollmentDTO.getAssignedBy()
            );

            // Log course from bundle enrollment
            logEnrollmentHistory(
                    enrollmentDTO.getUserId(),
                    null,
                    courseBundle.getCourseId(),
                    enrollmentDTO.getBundleId(),
                    enrollmentDTO.getDeadline(),
                    enrollmentDTO.getAssignedBy(),
                    now,
                    "ASSIGNED"
            );
        }
    }

    private String processGroupEnrollment(EnrollmentDTO enrollmentDTO, LocalDateTime now) {
        // Verify group exists and get all users in the group
        Group group = groupRepository.findById(enrollmentDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        List<UserGroup> userGroups = userGroupRepository.findAllByGroupId(group.getGroupId());
        if (userGroups.isEmpty()) {
            throw new ResourceNotFoundException("No users in the group");
        }

        // Verify manager has authority over all users in the group (batch fetch)
        List<Long> userIds = userGroups.stream().map(UserGroup::getUserId).collect(Collectors.toList());
        Map<Long, User> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        for (UserGroup userGroup : userGroups) {
            User user = users.get(userGroup.getUserId());
            if (user == null || !Objects.equals(user.getManagerId(), enrollmentDTO.getAssignedBy())) {
                throw new ResourceConflictException("Group has members that are not managed by you");
            }
        }

        if (enrollmentDTO.getCourseId() != null) {
            enrollGroupInCourse(enrollmentDTO, userGroups, now);
        } else if (enrollmentDTO.getBundleId() != null) {
            enrollGroupInBundle(enrollmentDTO, userGroups, now);
        } else {
            throw new InvalidRequestException("Either courseId or bundleId must be provided");
        }

        return "Enrollment Successful";
    }

    private void enrollGroupInCourse(EnrollmentDTO enrollmentDTO, List<UserGroup> userGroups, LocalDateTime now) {
        // Verify course exists
        if (!courseMicroserviceClient.courseExistsById(enrollmentDTO.getCourseId())) {
            throw new ResourceNotFoundException("Course Not Found");
        }

        // Check if already enrolled
        groupCourseEnrollmentRepository.findByGroupIdAndCourseIdAndStatusNotIn(
                        enrollmentDTO.getGroupId(),
                        enrollmentDTO.getCourseId(),
                        Arrays.asList("COMPLETED", "EXPIRED"))
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("Course is already assigned to the group");
                });

        // Create group course enrollment
        GroupCourseEnrollment groupCourseEnrollment = new GroupCourseEnrollment();
        groupCourseEnrollment.setGroupId(enrollmentDTO.getGroupId());
        groupCourseEnrollment.setCourseId(enrollmentDTO.getCourseId());
        groupCourseEnrollment.setDeadline(enrollmentDTO.getDeadline());
        groupCourseEnrollment.setStatus("ENROLLED");
        groupCourseEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
        groupCourseEnrollment.setAssignedAt(now);
        groupCourseEnrollmentRepository.save(groupCourseEnrollment);

        // Log group enrollment
        logEnrollmentHistory(
                null,
                enrollmentDTO.getGroupId(),
                enrollmentDTO.getCourseId(),
                null,
                enrollmentDTO.getDeadline(),
                enrollmentDTO.getAssignedBy(),
                now,
                "ASSIGNED"
        );

        // Enroll each user in the group
        for (UserGroup userGroup : userGroups) {
            // Update enrollment record
            createOrUpdateEnrollment(
                    userGroup.getUserId(),
                    enrollmentDTO.getCourseId(),  // Fixed bug: was using userId instead of courseId
                    enrollmentDTO.getAssignedBy()
            );

            // Log individual enrollment within group
            logEnrollmentHistory(
                    userGroup.getUserId(),
                    enrollmentDTO.getGroupId(),
                    enrollmentDTO.getCourseId(),
                    null,
                    enrollmentDTO.getDeadline(),
                    enrollmentDTO.getAssignedBy(),
                    now,
                    "ASSIGNED"
            );
        }
    }

    private void enrollGroupInBundle(EnrollmentDTO enrollmentDTO, List<UserGroup> userGroups, LocalDateTime now) {
        // Verify bundle exists
        if (!courseMicroserviceClient.bundleExistsById(enrollmentDTO.getBundleId())) {
            throw new ResourceNotFoundException("Bundle not found");
        }

        // Check if already enrolled
        groupBundleEnrollmentRepository.findByGroupIdAndBundleIdAndStatusNotIn(
                        enrollmentDTO.getGroupId(),
                        enrollmentDTO.getBundleId(),
                        Arrays.asList("COMPLETED", "EXPIRED"))
                .ifPresent(enrollment -> {
                    throw new ResourceConflictException("Bundle is already assigned to the group");
                });

        // Get courses in bundle
        List<CourseBundleDTO> courseBundleDTOs = getCoursesInBundle(enrollmentDTO.getBundleId());

        // Create group bundle enrollment
        GroupBundleEnrollment groupBundleEnrollment = new GroupBundleEnrollment();
        groupBundleEnrollment.setGroupId(enrollmentDTO.getGroupId());
        groupBundleEnrollment.setBundleId(enrollmentDTO.getBundleId());
        groupBundleEnrollment.setDeadline(enrollmentDTO.getDeadline());
        groupBundleEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
        groupBundleEnrollment.setAssignedAt(now);
        groupBundleEnrollment.setStatus("ENROLLED");
        groupBundleEnrollmentRepository.save(groupBundleEnrollment);

        // Log group bundle enrollment
        logEnrollmentHistory(
                null,
                enrollmentDTO.getGroupId(),
                null,
                enrollmentDTO.getBundleId(),
                enrollmentDTO.getDeadline(),
                enrollmentDTO.getAssignedBy(),
                now,
                "ASSIGNED"
        );

        // Process course enrollments for all users in group
        for (UserGroup userGroup : userGroups) {
            for (CourseBundleDTO courseBundle : courseBundleDTOs) {
                // Update enrollment record - Fixed bug: missing save call
                createOrUpdateEnrollment(
                        userGroup.getUserId(),
                        courseBundle.getCourseId(),
                        enrollmentDTO.getAssignedBy()
                );

                // Log individual course enrollment
                logEnrollmentHistory(
                        userGroup.getUserId(),
                        enrollmentDTO.getGroupId(),
                        courseBundle.getCourseId(),
                        enrollmentDTO.getBundleId(),
                        enrollmentDTO.getDeadline(),
                        enrollmentDTO.getAssignedBy(),
                        now,
                        "ASSIGNED"
                );
            }
        }
    }

    // Helper methods
    private List<CourseBundleDTO> getCoursesInBundle(Long bundleId) {
        List<CourseBundleDTO> courseBundleDTOs = courseMicroserviceClient.getAllCoursesByBundleId(bundleId).getBody();
        if (courseBundleDTOs == null || courseBundleDTOs.isEmpty()) {
            throw new ResourceNotFoundException("No courses in the bundle");
        }
        return courseBundleDTOs;
    }

    private void createOrUpdateEnrollment(Long userId, Long courseId, Long assignedBy) {
        Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(userId, courseId);
        if (existingEnrollment == null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUserId(userId);
            enrollment.setCourseId(courseId);
            enrollment.setAssignedBy(assignedBy);
            enrollment.setIsEnrolled(true);
            enrollmentRepository.save(enrollment);
        }
    }

    private void logEnrollmentHistory(
            Long userId,
            Long groupId,
            Long courseId,
            Long bundleId,
            LocalDateTime deadline,
            Long assignedBy,
            LocalDateTime recordedAt,
            String actionType) {

        EnrollmentHistory history = new EnrollmentHistory();
        history.setUserId(userId);
        history.setGroupId(groupId);
        history.setCourseId(courseId);
        history.setBundleId(bundleId);
        history.setDeadline(deadline);
        history.setAssignedBy(assignedBy);
        history.setActionType(actionType);
        history.setRecordedAt(recordedAt);
        enrollmentHistoryRepository.save(history);
    }
}
