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

        try {
            // Check if manager exists and has authorization in one step
            if (!userRepository.existsById(enrollmentDTO.getAssignedBy())) {
                throw new ResourceNotFoundException("Manager not found");
            }

            //IF USER ID IS NOT NULL
            if (enrollmentDTO.getUserId() != null) {
                // Get the user in a single database call
                User user = userRepository.findById(enrollmentDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Verify manager relationship
                if (!Objects.equals(user.getManagerId(), enrollmentDTO.getAssignedBy())) {
                    throw new InvalidRequestException("You cannot assign course to this user");
                }

                if (enrollmentDTO.getCourseId() != null) {
                    //CHECK IF COURSE EXISTS
                    if (!courseMicroserviceClient.courseExistsById(enrollmentDTO.getCourseId())) {
                        throw new ResourceNotFoundException("Course Not Found");
                    }
                    //CHECK IF ENROLLMENT ALREADY EXISTS
                    Optional<UserCourseEnrollment> existingUserCourseEnrollment = userCourseEnrollmentRepository.findByUserIdAndCourseIdAndStatusNotIn(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId(), Arrays.asList("COMPLETED", "EXPIRED"));
                    if (existingUserCourseEnrollment.isPresent()) {
                        throw new ResourceConflictException("This course is already assigned to the user");
                    }
                    //IF NOT ASSIGNED THAN ASSIGN
                    UserCourseEnrollment userCourseEnrollment = new UserCourseEnrollment();
                    userCourseEnrollment.setCourseId(enrollmentDTO.getCourseId());
                    userCourseEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                    userCourseEnrollment.setStatus("ENROLLED");
                    userCourseEnrollment.setAssignedAt(LocalDateTime.now());
                    userCourseEnrollment.setDeadline(enrollmentDTO.getDeadline());
                    userCourseEnrollmentRepository.save(userCourseEnrollment);

                    //CHECK IN ENROLLMENT TABLE IF NOT ASSIGNED THAN ASSIGN
                    Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId());
                    if (existingEnrollment == null) {
                        Enrollment enrollment = new Enrollment();
                        enrollment.setUserId(enrollmentDTO.getUserId());
                        enrollment.setCourseId(enrollmentDTO.getCourseId());
                        enrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                        enrollment.setIsEnrolled(true);
                        enrollmentRepository.save(enrollment);
                    }
                    // LOG IN LOG TABLE
                    EnrollmentHistory logUserCourseEnrollmentHistory = new EnrollmentHistory();
                    logUserCourseEnrollmentHistory.setUserId(enrollmentDTO.getUserId());
                    logUserCourseEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                    logUserCourseEnrollmentHistory.setCourseId(enrollmentDTO.getCourseId());
                    logUserCourseEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                    logUserCourseEnrollmentHistory.setActionType("ASSIGNED");
                    logUserCourseEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                    enrollmentHistoryRepository.save(logUserCourseEnrollmentHistory);
                } else if (enrollmentDTO.getBundleId() != null) {

                    //CHECK IF BUNDLE EXISTS
                    if (!courseMicroserviceClient.bundleExistsById(enrollmentDTO.getBundleId())) {
                        throw new ResourceNotFoundException("Bundle not found");
                    }

                    // CHECK IF BUNDLE IS ALREADY ASSIGNED TO THE USER
                    Optional<UserBundleEnrollment> existingUserBundleEnrollment = userBundleEnrollmentRepository.findByUserIdAndBundleIdAndStatusNotIn(enrollmentDTO.getUserId(), enrollmentDTO.getBundleId(), Arrays.asList("COMPLETED", "EXPIRED"));
                    if (existingUserBundleEnrollment.isPresent()) {
                        throw new ResourceConflictException("Bundle is already assigned to the user");
                    }
                    UserBundleEnrollment userBundleEnrollment = new UserBundleEnrollment();
                    userBundleEnrollment.setUserId(enrollmentDTO.getUserId());
                    userBundleEnrollment.setDeadline(enrollmentDTO.getDeadline());
                    userBundleEnrollment.setStatus("ENROLLED");
                    userBundleEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                    userBundleEnrollment.setBundleId(enrollmentDTO.getBundleId());
                    userBundleEnrollment.setAssignedAt(LocalDateTime.now());
                    userBundleEnrollmentRepository.save(userBundleEnrollment);

                    //LOG IN THE LOG TABLE

                    EnrollmentHistory userBundleEnrollmentHistory = new EnrollmentHistory();
                    userBundleEnrollmentHistory.setUserId(enrollmentDTO.getUserId());
                    userBundleEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                    userBundleEnrollmentHistory.setCourseId(enrollmentDTO.getBundleId());
                    userBundleEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                    userBundleEnrollmentHistory.setActionType("ASSIGNED");
                    userBundleEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                    enrollmentHistoryRepository.save(userBundleEnrollmentHistory);

                    //GET ALL COURSES FOR THAT BUNDLE
                    List<CourseBundleDTO> courseBundleDTOList = courseMicroserviceClient.getAllCoursesByBundleId(enrollmentDTO.getBundleId()).getBody();
                    if (courseBundleDTOList != null && courseBundleDTOList.isEmpty()) {
                        throw new ResourceNotFoundException("No courses in the bundle");
                    }

                    //Loop through the course bundles
                    for (CourseBundleDTO courseBundleDTO : courseBundleDTOList) {
                        //USER ENROLL TO BUNDLE
                        Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(enrollmentDTO.getUserId(), courseBundleDTO.getCourseId());
                        if (existingEnrollment == null) {
                            Enrollment enrollment = new Enrollment();
                            enrollment.setUserId(enrollmentDTO.getUserId());
                            enrollment.setCourseId(courseBundleDTO.getCourseId());
                            enrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                            enrollment.setIsEnrolled(true);
                            enrollmentRepository.save(enrollment);
                        }
                        // LOG IN LOG TABLE
                        EnrollmentHistory userCourseBundleEnrollmentHistory = new EnrollmentHistory();
                        userCourseBundleEnrollmentHistory.setUserId(enrollmentDTO.getUserId());
                        userCourseBundleEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                        userCourseBundleEnrollmentHistory.setCourseId(courseBundleDTO.getCourseId());
                        userCourseBundleEnrollmentHistory.setBundleId(enrollmentDTO.getBundleId());
                        userCourseBundleEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                        userCourseBundleEnrollmentHistory.setActionType("ASSIGNED");
                        userCourseBundleEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                        enrollmentHistoryRepository.save(userCourseBundleEnrollmentHistory);
                    }

                }
            }

            //IF GROUP ID IS NOT NULL
            if (enrollmentDTO.getGroupId() != null) {
                //CHECK IF GROUP EXISTS
                Group group = groupRepository.findById(enrollmentDTO.getGroupId())
                        .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

                //FIND USERS IN THE GROUP
                List<UserGroup> userGroups = userGroupRepository.findAllByGroupId(group.getGroupId());
                if (userGroups.isEmpty()) {
                    throw new ResourceNotFoundException("No user in the group");
                }
                //CHECK USERS EXISTS AND THEIR MANAGER IS THE SAME
                for (UserGroup userGroup : userGroups) {
                    User user = userRepository.findById(enrollmentDTO.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    if (!Objects.equals(user.getManagerId(), enrollmentDTO.getAssignedBy())) {
                        throw new ResourceConflictException("Group has members that are not managed by you.");
                    }
                }
                if (enrollmentDTO.getCourseId() != null) {
                    if (!courseMicroserviceClient.courseExistsById(enrollmentDTO.getCourseId())) {
                        throw new ResourceNotFoundException("Course Not Found");
                    }
                    //CHECK IF ENROLLMENT ALREADY EXISTS
                    GroupCourseEnrollment existingGroupCourseEnrollment = groupCourseEnrollmentRepository.findByGroupIdAndCourseIdAndStatusNotIn(enrollmentDTO.getGroupId(), enrollmentDTO.getCourseId(), Arrays.asList("COMPLETED", "EXPIRED"))
                            .orElseThrow(() -> new ResourceNotFoundException("User already enrolled in the group"));
                    //GROUP ENROLL TO COURSE
                    GroupCourseEnrollment groupCourseEnrollment = new GroupCourseEnrollment();
                    groupCourseEnrollment.setCourseId(enrollmentDTO.getCourseId());
                    groupCourseEnrollment.setGroupId(enrollmentDTO.getGroupId());
                    groupCourseEnrollment.setDeadline(enrollmentDTO.getDeadline());
                    groupCourseEnrollment.setStatus("ENROLLED");
                    groupCourseEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                    groupCourseEnrollment.setAssignedAt(LocalDateTime.now());
                    groupCourseEnrollmentRepository.save(groupCourseEnrollment);

                    EnrollmentHistory groupCourseEnrollmentHistory = new EnrollmentHistory();
                    groupCourseEnrollmentHistory.setGroupId(enrollmentDTO.getGroupId());
                    groupCourseEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                    groupCourseEnrollmentHistory.setCourseId(enrollmentDTO.getCourseId());
                    groupCourseEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                    groupCourseEnrollmentHistory.setActionType("ASSIGNED");
                    groupCourseEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                    enrollmentHistoryRepository.save(groupCourseEnrollmentHistory);

                    for (UserGroup userGroup : userGroups) {
                        Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(userGroup.getUserId(), enrollmentDTO.getCourseId());
                        if (existingEnrollment == null) {
                            Enrollment enrollment = new Enrollment();
                            enrollment.setUserId(userGroup.getUserId());
                            enrollment.setCourseId(enrollmentDTO.getUserId());
                            enrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                            enrollment.setIsEnrolled(true);
                            enrollmentRepository.save(enrollment);
                        }

                        EnrollmentHistory userGroupCourseEnrollmentHistory = new EnrollmentHistory();
                        userGroupCourseEnrollmentHistory.setUserId(userGroup.getUserId());
                        userGroupCourseEnrollmentHistory.setGroupId(enrollmentDTO.getGroupId());
                        userGroupCourseEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                        userGroupCourseEnrollmentHistory.setCourseId(enrollmentDTO.getCourseId());
                        userGroupCourseEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                        userGroupCourseEnrollmentHistory.setActionType("ASSIGNED");
                        userGroupCourseEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                        enrollmentHistoryRepository.save(userGroupCourseEnrollmentHistory);

                    }
                } else if (enrollmentDTO.getBundleId() != null) {
                    //CHECK IF BUNDLE EXISTS
                    if (!courseMicroserviceClient.bundleExistsById(enrollmentDTO.getBundleId())) {
                        throw new ResourceNotFoundException("Bundle not found");
                    }
                    //CHECK IF BUNDLE IS ALREADY ASSIGNED TO GROUP
                    Optional<GroupBundleEnrollment> existingGroupBundleEnrollment = groupBundleEnrollmentRepository.findByGroupIdAndBundleIdAndStatusNotIn(enrollmentDTO.getGroupId(), enrollmentDTO.getBundleId(), Arrays.asList("COMPLETED", "EXPIRED"));
                    if (existingGroupBundleEnrollment.isPresent()) {
                        throw new ResourceConflictException("Bundle is already assigned to the group");
                    }

                    //GET ALL COURSES IN THE BUNDLE
                    List<CourseBundleDTO> courseBundleDTOList = courseMicroserviceClient.getAllCoursesByBundleId(enrollmentDTO.getBundleId()).getBody();
                    if (courseBundleDTOList.isEmpty()) {
                        throw new ResourceNotFoundException("No courses in the bundle");
                    }
                    //GROUP ENROLL TO BUNDLE
                    GroupBundleEnrollment groupBundleEnrollment = new GroupBundleEnrollment();
                    groupBundleEnrollment.setGroupId(enrollmentDTO.getGroupId());
                    groupBundleEnrollment.setBundleId(enrollmentDTO.getBundleId());
                    groupBundleEnrollment.setDeadline(enrollmentDTO.getDeadline());
                    groupBundleEnrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
                    groupBundleEnrollment.setAssignedAt(LocalDateTime.now());
                    groupBundleEnrollment.setStatus("ENROLLED");
                    groupBundleEnrollmentRepository.save(groupBundleEnrollment);

                    //LOG GROUP BUNDLE ENROLLMENT
                    EnrollmentHistory groupBundleEnrollmentHistory = new EnrollmentHistory();
                    groupBundleEnrollmentHistory.setGroupId(enrollmentDTO.getGroupId());
                    groupBundleEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                    groupBundleEnrollmentHistory.setBundleId(enrollmentDTO.getBundleId());
                    groupBundleEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                    groupBundleEnrollmentHistory.setActionType("ASSIGNED");
                    groupBundleEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                    enrollmentHistoryRepository.save(groupBundleEnrollmentHistory);

                    //LOOP COURSE BUNDLE COURSES FOR ALL USERS IN THE GROUP
                    for (CourseBundleDTO courseBundleDTO : courseBundleDTOList) {
                        for (UserGroup userGroup : userGroups) {
                            Enrollment existingEnrollment = enrollmentRepository.getByUserIdAndCourseId(userGroup.getUserId(), courseBundleDTO.getCourseId());
                            if (existingEnrollment == null) {
                                Enrollment enrollment = new Enrollment();
                                enrollment.setUserId(userGroup.getUserId());
                                enrollment.setCourseId(courseBundleDTO.getCourseId());
                                enrollment.setAssignedBy(enrollment.getAssignedBy());
                                enrollment.setIsEnrolled(true);
                            }

                            //LOG GROUP USER BUNDLE COURSE ENROLLMENT
                            EnrollmentHistory groupUserBundleCourseEnrollmentHistory = new EnrollmentHistory();
                            groupUserBundleCourseEnrollmentHistory.setGroupId(enrollmentDTO.getGroupId());
                            groupUserBundleCourseEnrollmentHistory.setUserId(userGroup.getUserId());
                            groupUserBundleCourseEnrollmentHistory.setBundleId(enrollmentDTO.getBundleId());
                            groupUserBundleCourseEnrollmentHistory.setCourseId(courseBundleDTO.getCourseId());
                            groupUserBundleCourseEnrollmentHistory.setDeadline(enrollmentDTO.getDeadline());
                            groupUserBundleCourseEnrollmentHistory.setAssignedBy(enrollmentDTO.getAssignedBy());
                            groupUserBundleCourseEnrollmentHistory.setActionType("ASSIGNED");
                            groupUserBundleCourseEnrollmentHistory.setRecordedAt(LocalDateTime.now());
                            enrollmentHistoryRepository.save(groupUserBundleCourseEnrollmentHistory);
                        }
                    }
                }
            }
            return "Enrollment Successful";
        } catch (ResourceNotFoundException | ResourceConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }
}
