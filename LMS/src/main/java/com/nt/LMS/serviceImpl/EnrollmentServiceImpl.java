package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.*;
import com.nt.LMS.entities.*;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.*;
import com.nt.LMS.service.EnrollmentService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

//    @Transactional
//    @Override
//    public String updateEnrollment(UpdateEnrollmentDTO updateEnrollmentDTO) {
//        //UPDATE FOR USER ENROLLMENT
//        if (updateEnrollmentDTO.getUserId() != null) {
//            //VALIDATE USER AND MANAGER RELATIONSHIP
//            User user = userRepository.findById(updateEnrollmentDTO.getUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//            if (!Objects.equals(user.getManagerId(), updateEnrollmentDTO.getManagerId())) {
//                throw new InvalidRequestException("You cannot update enrollments of this user");
//            }
//
//            //USER COURSE ENROLLMENT UPDATE
//            if (updateEnrollmentDTO.getCourseId() != null) {
//                EnrollmentHistory updateUserCourseEnrollmentHistory = new EnrollmentHistory();
//                String remarks = "UPDATED ";
//                //CHECK IF ENROLLMENT EXISTS
//               UserCourseEnrollment userCourseEnrollment = userCourseEnrollmentRepository.findByUserIdAndCourseIdAndStatusNotIn(updateEnrollmentDTO.getUserId(), updateEnrollmentDTO.getCourseId(), Arrays.asList("COMPLETED", "EXPIRED"))
//                       .orElseThrow(() -> new ResourceNotFoundException("No Enrollment Found"));
//
//               if (updateEnrollmentDTO.getDeadline() != null || updateEnrollmentDTO.getStatus() != null) {
//                   if(updateEnrollmentDTO.getStatus() != null) {
//                       if (!List.of("ENROLLED", "IN PROGRESS", "COMPLETED", "UNENROLLED")
//                               .contains(updateEnrollmentDTO.getStatus())) {
//                           throw new InvalidRequestException("Invalid Status Provided");
//                       }
//                       if (List.of("ENROLLED", "IN PROGRESS").contains(userCourseEnrollment.getStatus())) {
//                           userCourseEnrollment.setStatus(updateEnrollmentDTO.getStatus());
//                           remarks += "Status ";
//                           userCourseEnrollmentRepository.save(userCourseEnrollment);
//                       }
//                   }
//
//                   if(updateEnrollmentDTO.getDeadline() != null) {
//                       userCourseEnrollment.setDeadline(updateEnrollmentDTO.getDeadline());
//                       updateUserCourseEnrollmentHistory.setDeadline(updateEnrollmentDTO.getDeadline());
//                       remarks += "Deadline ";
//                       userCourseEnrollmentRepository.save(userCourseEnrollment);
//                   }
//
//                   updateUserCourseEnrollmentHistory.setUserId(updateEnrollmentDTO.getUserId());
//                   updateUserCourseEnrollmentHistory.setCourseId(updateEnrollmentDTO.getCourseId());
//                   updateUserCourseEnrollmentHistory.setAssignedBy(userCourseEnrollment.getAssignedBy());
//                   updateUserCourseEnrollmentHistory.setStatus("UPDATED");
//                   if(updateEnrollmentDTO.getStatus().equals("UNENROLLED")) {
//                       updateUserCourseEnrollmentHistory.setStatus("UNASSIGNED");
//                       remarks = "Course Unenrolled";
//                   }
//                   updateUserCourseEnrollmentHistory.setRecordedAt(LocalDateTime.now());
//                   updateUserCourseEnrollmentHistory.setRemarks(remarks);
//
//               }
//            }
//        }
//        return "";
//    }



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
                "ENROLLED"
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
                "ENROLLED"
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
                    "ENROLLED"
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
                "ENROLLED"
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
                    "ENROLLED"
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
                "ENROLLED"
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
                        "ENROLLED"
                );
            }
        }
    }
    @Override
    public long countEnrollments() {
        return enrollmentRepository.count();
    }

    @Override
    public EnrollmentDashBoardStatsDTO getEnrollmentStats() {
        try {
            EnrollmentDashBoardStatsDTO enrollmentDashBoardStatsDTO = new EnrollmentDashBoardStatsDTO();
            Long usersEnrolled = userCourseEnrollmentRepository.countByStatusNotIn(List.of("COMPLETED", "EXPIRED", "UNENROLLED")) + userBundleEnrollmentRepository.countByStatusNotIn(List.of("COMPLETED", "EXPIRED", "UNENROLLED"));
            Long groupsEnrolled = groupCourseEnrollmentRepository.countByStatusNotIn(List.of("COMPLETED", "EXPIRED", "UNENROLLED")) + groupBundleEnrollmentRepository.countByStatusNotIn(List.of("COMPLETED", "EXPIRED", "UNENROLLED"));
            Long popularCourseId = enrollmentRepository.findMostFrequentEnrolledCourseId();
            Long courseCompletions = enrollmentHistoryRepository.countByStatusIn(List.of("COMPLETED"));
            if(usersEnrolled == 0 && groupsEnrolled == 0) {
                throw new ResourceNotFoundException("No Enrollments");
            }

            List<UserCourseEnrollment> enrollments = userCourseEnrollmentRepository.findAll();

            long totalActiveUsers = enrollments.stream()
                    .map(UserCourseEnrollment::getUserId)
                    .distinct() // optional: ensures each user is counted only once
                    .map(userRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> user.isActive() && user.getUserId() != 1)
                    .count();

            String popularCourse = Objects.requireNonNull(courseMicroserviceClient.getCourseNameById(popularCourseId).getBody());
            Long totalEnrollments =  usersEnrolled + groupsEnrolled;
            enrollmentDashBoardStatsDTO.setUsersEnrolled(totalActiveUsers);
            enrollmentDashBoardStatsDTO.setGroupsEnrolled(0L);
            enrollmentDashBoardStatsDTO.setTotalEnrollments(enrollmentRepository.count());
            enrollmentDashBoardStatsDTO.setCourseCompletions(46L);
            enrollmentDashBoardStatsDTO.setTopEnrolledCourse("N/A");
            enrollmentDashBoardStatsDTO.setUpcomingDeadlines(7L);
            enrollmentDashBoardStatsDTO.setCompletionRate(99L);
            return enrollmentDashBoardStatsDTO;
        } catch (FeignException e) {
            throw new RuntimeException("Error contacting course service");
        }
        catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    @Override
    public List<UserEnrollmentsDTO> getEnrollmentsForUser() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> user.isActive() && user.getUserId() != 1)
                .collect(Collectors.toList());
        if(users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        List<UserEnrollmentsDTO> userEnrollmentsDTOS = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(User user : users) {
            int upcomingDeadlines = 0;
            UserEnrollmentsDTO userEnrollmentsDTO = new UserEnrollmentsDTO();
            long courseEnrollments = userCourseEnrollmentRepository.countByUserIdAndStatusNotIn(user.getUserId(), List.of("EXPIRED", "UNENROLLED", "COMPLETED"));
            long bundleEnrollments = userBundleEnrollmentRepository.countByUserIdAndStatusNotIn(user.getUserId(), List.of("EXPIRED", "UNENROLLED", "COMPLETED"));
            if (courseEnrollments != 0) {
                List<UserCourseEnrollment> userCourseEnrollments = userCourseEnrollmentRepository.findByUserId(user.getUserId());
                List<EnrolledCoursesDTO> enrolledCoursesDTOS = new ArrayList<>();
                for(UserCourseEnrollment userCourseEnrollment : userCourseEnrollments) {
                    ResponseEntity<String> response = courseMicroserviceClient.getCourseNameById(userCourseEnrollment.getCourseId());
                    String courseName = (response != null && response.getBody() != null) ? response.getBody() : "Unknown Course";
                    EnrolledCoursesDTO enrolledCoursesDTO = new EnrolledCoursesDTO();
                    enrolledCoursesDTO.setCourseId(userCourseEnrollment.getCourseId());
                    enrolledCoursesDTO.setCourseName(courseName);
                    enrolledCoursesDTO.setEnrollmentDate(userCourseEnrollment.getAssignedAt());
                    enrolledCoursesDTO.setDeadline(userCourseEnrollment.getDeadline());
                    //enrolledCoursesDTO.setProgress(98F);
                    Double progress = courseMicroserviceClient.getCourseProgress((int) user.getUserId(), userCourseEnrollment.getCourseId().intValue()).getBody();
                    enrolledCoursesDTO.setProgress(progress != null ? progress.floatValue() : 0F);
                    enrolledCoursesDTOS.add(enrolledCoursesDTO);

                    LocalDateTime deadline = enrolledCoursesDTO.getDeadline();

                    if (deadline != null && !deadline.isBefore(now) && deadline.isBefore(now.plusDays(7))) {
                        upcomingDeadlines += 1;
                    }
                }
                userEnrollmentsDTO.setEnrolledCoursesList(enrolledCoursesDTOS);
            }
            if (bundleEnrollments != 0) {
                List<UserBundleEnrollment> userBundleEnrollments = userBundleEnrollmentRepository.findByUserId(user.getUserId());
                List<EnrolledBundlesDTO> enrolledBundlesDTOS = new ArrayList<>();
                for(UserBundleEnrollment userBundleEnrollment : userBundleEnrollments) {
                    ResponseEntity<String> response = courseMicroserviceClient.getBundleNameById(userBundleEnrollment.getBundleId());
                    String bundleName = (response != null && response.getBody() != null) ? response.getBody() : "Unknown Course";

                    EnrolledBundlesDTO enrolledBundlesDTO = new EnrolledBundlesDTO();
                    enrolledBundlesDTO.setBundleName(bundleName);
                    enrolledBundlesDTO.setBundleId(userBundleEnrollment.getBundleId());
                    enrolledBundlesDTO.setEnrollmentDate(userBundleEnrollment.getAssignedAt());
                    enrolledBundlesDTO.setProgress(50F);
                    enrolledBundlesDTO.setDeadline(userBundleEnrollment.getDeadline());
                    enrolledBundlesDTOS.add(enrolledBundlesDTO);

                    LocalDateTime deadline = enrolledBundlesDTO.getDeadline();

                    if (deadline != null && !deadline.isBefore(now) && deadline.isBefore(now.plusDays(7))) {
                        upcomingDeadlines += 1;
                    }
                }
                userEnrollmentsDTO.setEnrolledBundlesList(enrolledBundlesDTOS);
            }
            userEnrollmentsDTO.setUserId(user.getUserId());
            userEnrollmentsDTO.setUserName(user.getFirstName() + " " + user.getLastName());
            userEnrollmentsDTO.setStatus(true);
            userEnrollmentsDTO.setCourseEnrollments(courseEnrollments);
            userEnrollmentsDTO.setBundleEnrollments(bundleEnrollments);
            userEnrollmentsDTO.setAverageCompletion(50F);
            userEnrollmentsDTO.setUpcomingDeadlines(upcomingDeadlines);
            userEnrollmentsDTOS.add(userEnrollmentsDTO);
        }
        return userEnrollmentsDTOS;
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
        history.setStatus(actionType);
        history.setRecordedAt(recordedAt);
        enrollmentHistoryRepository.save(history);
    }
}
