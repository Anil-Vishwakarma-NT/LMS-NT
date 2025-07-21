package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.GroupDTOConverter;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupCourseOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupSummaryOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupUserOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDto;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserGroupOutDTO;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Group;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.entities.UserGroup;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.GroupRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nt.user_service_lms.constants.GroupConstants.GROUP_CREATED;
import static com.nt.user_service_lms.constants.GroupConstants.GROUP_DELETED;
import static com.nt.user_service_lms.constants.GroupConstants.GROUP_FAILURE;
import static com.nt.user_service_lms.constants.GroupConstants.GROUP_NOT_FOUND;
import static com.nt.user_service_lms.constants.GroupConstants.USER_ADDED_TO_GROUP;
import static com.nt.user_service_lms.constants.GroupConstants.USER_NOT_FOUND_IN_GROUP;
import static com.nt.user_service_lms.constants.GroupConstants.USER_REMOVED_SUCCESSFULLY;
import static com.nt.user_service_lms.constants.UserConstants.USER_NOT_FOUND;

/**
 * Service implementation for managing user groups in the Learning Management System.
 * This class provides comprehensive group management functionality including:
 * - Creating and deleting groups
 * - Adding and removing users from groups
 * - Managing group-course enrollments
 * - Retrieving group information and statistics
 *
 * @version 1.0
 * @since 2024
 */
@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    /**
     * Repository for group-related database operations.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Repository for user-related database operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Converter for transforming User entities to DTOs.
     */
    @Autowired
    private UserDTOConverter userDTOConverter;

    /**
     * Repository for managing user-group relationships.
     */
    @Autowired
    private UserGroupRepository userGroupRepository;

    /**
     * Repository for managing course enrollments.
     */
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Feign client for communicating with the Course microservice.
     */
    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    /**
     * Converter for transforming Group entities to DTOs.
     */
    @Autowired
    private GroupDTOConverter groupDTOConverter;

    /**
     * Creates a new group with the specified name and creator.
     * Optionally adds initial employees to the group if provided.
     *
     * @param groupName  the name of the group to be created
     * @param username   the email of the user creating the group
     * @param employeeId list of employee IDs to add to the group initially
     * @return StandardResponseOutDTO containing success message and group creation status
     * @throws UnauthorizedAccessException if the user creating the group is not found
     * @throws RuntimeException            if any error occurs during group creation
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> createGroup(
            final String groupName, final String username, final List<Long> employeeId
    ) {
        try {
            log.info("Attempting to create a group with name: {} by user: {}", groupName, username);
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));

            Group group = new Group(groupName, user.getUserId());
            group = groupRepository.save(group);
            log.info("Group '{}' created successfully by '{}'", groupName, username);

            if (!employeeId.isEmpty()) {
                Group finalGroup = group;
                employeeId.forEach(employee -> {
                    UserGroup userGroup = new UserGroup(employee, finalGroup.getGroupId());
                    userGroupRepository.save(userGroup);
                });
            }

            MessageOutDto messageOutDto = new MessageOutDto(GROUP_CREATED);
            return StandardResponseOutDTO.success(messageOutDto, GROUP_CREATED);
        } catch (Exception e) {
            log.error("Error while creating group '{}' by '{}'", groupName, username, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a group and all associated user-group relationships and enrollments.
     * Performs soft deletion to maintain data integrity.
     *
     * @param groupId the unique identifier of the group to be deleted
     * @return StandardResponseOutDTO containing success message
     * @throws ResourceNotFoundException if the group with the specified ID is not found
     * @throws RuntimeException          if any error occurs during group deletion
     */
    @Override
    @Transactional
    public StandardResponseOutDTO<MessageOutDto> deleteGroup(final long groupId) {
        try {
            log.info("Attempting to delete group with ID: {}", groupId);
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

            enrollmentRepository.softDeleteByGroupId(groupId);
            userGroupRepository.softDeleteByGroupId(groupId);
            groupRepository.softDeleteByGroupId(groupId);

            log.info("Group with ID: {} deleted successfully", groupId);
            MessageOutDto messageOutDto = new MessageOutDto(GROUP_DELETED);
            return StandardResponseOutDTO.success(messageOutDto, null);
        } catch (Exception e) {
            log.error("Error while deleting group with ID: {}", groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Adds users to a group and optionally enrolls them in specified courses.
     * If users are already in the group, reactivates their membership.
     *
     * @param groupInDTO contains group ID, employee IDs, and course IDs
     * @param username   the email of the user performing the operation
     * @return StandardResponseOutDTO containing success message
     * @throws UnauthorizedAccessException if the requesting user is not found
     * @throws ResourceNotFoundException   if the group or any user is not found
     * @throws RuntimeException            if any error occurs during the operation
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> addUserToGroup(final GroupInDTO groupInDTO, final String username) {
        try {
            log.info("Adding user ID: to group ID: {}", groupInDTO.getGroupId());

            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));

            if (groupRepository.findById(groupInDTO.getGroupId()).isEmpty()) {
                throw new ResourceNotFoundException(GROUP_NOT_FOUND);
            }

            for (Long id : groupInDTO.getEmployees()) {
                if (userRepository.findById(id).isEmpty()) {
                    throw new ResourceNotFoundException(USER_NOT_FOUND);
                }
                Optional<UserGroup> usergroup = userGroupRepository.findByUserIdAndGroupId(id, groupInDTO.getGroupId());
                if (usergroup.isPresent()) {
                    usergroup.get().setActive(true);
                    userGroupRepository.save(usergroup.get());
                } else {
                    UserGroup userGroup = new UserGroup(id, groupInDTO.getGroupId());
                    userGroupRepository.save(userGroup);
                }
            }

            if (!groupInDTO.getCourses().isEmpty()) {
                for (Long courseId : groupInDTO.getCourses()) {
                    for (Long userId : groupInDTO.getEmployees()) {
                        Optional<Enrollment> existing = enrollmentRepository.findByGroupIdAndUserIdAndCourseId(
                                groupInDTO.getGroupId(), userId, courseId
                        );
                        if (existing.isPresent()) {
                            existing.get().setActive(true);
                            enrollmentRepository.save(existing.get());
                        } else {
                            Enrollment enrol = new Enrollment();
                            enrol.setGroupId(groupInDTO.getGroupId());
                            enrol.setUserId(userId);
                            enrol.setCourseId(courseId);
                            enrol.setAssignedBy(user.getUserId());
                            enrol.setAssignedAt(groupInDTO.getAssignedAt());
                            enrol.setDeadline(groupInDTO.getDeadline());
                            enrol.setStatus("active");
                            enrol.setEnrollmentSource("GROUP");
                            enrol.setCreatedAt(groupInDTO.getAssignedAt());
                            enrol.setUpdatedAt(groupInDTO.getAssignedAt());
                            enrollmentRepository.save(enrol);
                        }
                    }
                }
            }

            MessageOutDto messageOutDto = new MessageOutDto(USER_ADDED_TO_GROUP);
            return StandardResponseOutDTO.success(messageOutDto, null);
        } catch (Exception e) {
            log.error("Error adding user ID: to group ID: {}", groupInDTO.getGroupId(), e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Updates the name of an existing group.
     *
     * @param groupId   the unique identifier of the group to be updated
     * @param groupName the new name for the group
     * @return StandardResponseOutDTO containing success message
     * @throws ResourceNotFoundException if the group with the specified ID is not found
     * @throws RuntimeException          if any error occurs during the update operation
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> updateGroup(final long groupId, final String groupName) {
        try {
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isPresent()) {
                group.get().setGroupName(groupName);
                groupRepository.save(group.get());
            } else {
                throw new ResourceNotFoundException("Group Not found");
            }
            MessageOutDto messageOutDto = new MessageOutDto("Group updated");
            return StandardResponseOutDTO.success(messageOutDto, null);
        } catch (Exception e) {
            log.error("Error in updating group");
            throw new RuntimeException("Group not updated", e);
        }
    }

    /**
     * Removes a user from a group by performing soft deletion.
     * Also removes all associated enrollments for that user in the group.
     *
     * @param userId  the unique identifier of the user to be removed
     * @param groupId the unique identifier of the group
     * @return StandardResponseOutDTO containing success message
     * @throws ResourceNotFoundException if the user is not found in the group
     * @throws RuntimeException          if any error occurs during the removal operation
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> removeUserFromGroup(final long userId, final long groupId) {
        try {
            UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_IN_GROUP));

            userGroupRepository.softDeleteByGroupIdAndUserId(userGroup.getGroupId(), userGroup.getUserId());
            enrollmentRepository.softDeleteByGroupIdAndUserId(userGroup.getGroupId(), userGroup.getUserId());

            MessageOutDto messageOutDto = new MessageOutDto(USER_REMOVED_SUCCESSFULLY);
            return StandardResponseOutDTO.success(messageOutDto, null);
        } catch (Exception e) {
            log.error("Error removing user ID: {} from group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Retrieves courses available to a specific user within a group.
     * Returns courses that are assigned to the group but not yet enrolled by the user.
     *
     * @param groupId the unique identifier of the group
     * @param userId  the unique identifier of the user
     * @return StandardResponseOutDTO containing list of available courses
     * @throws RuntimeException if any error occurs during course retrieval
     */
    @Override
    public StandardResponseOutDTO<List<CourseInfoOutDTO>> getUserCourses(final long groupId, final long userId) {
        try {
            List<Enrollment> enrols = enrollmentRepository.findByGroupIdAndUserId(groupId, userId);
            System.out.println("Enrols has values  " + enrols.isEmpty() + " " + groupId + "  " + userId);
            log.info("enrols fetched");

            List<CourseInfoOutDTO> courses = courseMicroserviceClient.getCourseInfo().getBody().getData();
            List<Enrollment> groupcourses = enrollmentRepository.findByGroupId(groupId);
            log.info("courses fetched");

            Set<Long> enrolledCourseIds = enrols.stream()
                    .map(Enrollment::getCourseId)
                    .collect(Collectors.toSet());
            Set<Long> enrolledGroupCourseIds = groupcourses.stream()
                    .map(Enrollment::getCourseId)
                    .collect(Collectors.toSet());
            log.info("enrolled courses  fetched");

            List<CourseInfoOutDTO> notEnrolledCourses = courses.stream()
                    .filter(
                            course -> !enrolledCourseIds.contains(
                                    course.getCourseId()) && enrolledGroupCourseIds.contains(course.getCourseId()
                            )
                    )
                    .collect(Collectors.toList());

            return StandardResponseOutDTO.success(notEnrolledCourses, null);
        } catch (Exception e) {
            log.error("Error collecting courses for  user ID: {} from group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Retrieves groups associated with a user based on their role.
     * Admin users can see all groups, while regular users see only groups they created.
     *
     * @param email the email address of the user
     * @return StandardResponseOutDTO containing list of groups accessible to the user
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException          if any error occurs during group retrieval
     */
    @Override
    public StandardResponseOutDTO<List<GroupOutDTO>> getGroups(final String email) {
        try {
            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

            List<GroupOutDTO> groupOutList = new ArrayList<>();
            if (user.getUserId() == UserConstants.getAdminId()) {
                List<Group> adminGroups = groupRepository.findAll();
                for (Group group : adminGroups) {
                    if (group.isActive()) {
                        GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                                user.getFirstName() + " " + user.getLastName());
                        groupOutList.add(gout);
                    }
                }
            } else {
                List<Group> userGroups = groupRepository.findByCreatorId(user.getUserId());
                for (Group group : userGroups) {
                    if (group.isActive()) {
                        GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                                user.getFirstName() + " " + user.getLastName());
                        groupOutList.add(gout);
                    }
                }
            }
            return StandardResponseOutDTO.success(groupOutList, "Group fetched Successfully");
        } catch (Exception e) {
            log.error("Error fetching groups for user with email: {}", email, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all groups in the system along with their creator information.
     *
     * @return StandardResponseOutDTO containing list of all groups
     * @throws ResourceNotFoundException if any group creator is not found
     * @throws RuntimeException          if any error occurs during group retrieval
     */
    @Override
    public StandardResponseOutDTO<List<GroupOutDTO>> getAllGroups() {
        try {
            List<Group> groups = groupRepository.findAll();
            List<GroupOutDTO> groupOutList = new ArrayList<>();

            for (Group group : groups) {
                User creator = userRepository.findById(group.getCreatorId())
                        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                        creator.getFirstName() + " " + creator.getLastName());
                groupOutList.add(gout);
            }

            return StandardResponseOutDTO.success(groupOutList, "All Groups fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all groups", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the total count of groups in the system.
     *
     * @return the total number of groups
     */
    @Override
    public long countGroups() {
        return groupRepository.count();
    }

    /**
     * Retrieves all active groups in the system.
     *
     * @return StandardResponseOutDTO containing list of active groups
     * @throws ResourceNotFoundException if any group creator is not found
     */
    @Override
    public StandardResponseOutDTO<List<GroupOutDTO>> getAllActiveGroups() {
        List<Group> groups = groupRepository.findByIsActiveTrue();
        if (groups.isEmpty()) {
            return StandardResponseOutDTO.success(null, "No Groups Found");
        }
        List<GroupOutDTO> groupOutDTOS = new ArrayList<>();
        for (Group group : groups) {
            User creator = userRepository.findById(group.getCreatorId())
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
            GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                    creator.getFirstName() + " " + creator.getLastName());
            groupOutDTOS.add(gout);
        }

        return StandardResponseOutDTO.success(groupOutDTOS, "All Groups fetched successfully");
    }

    /**
     * Retrieves summary information for the 5 most recently created groups.
     *
     * @return StandardResponseOutDTO containing list of recent group summaries
     */
    @Override
    public StandardResponseOutDTO<List<GroupSummaryOutDTO>> getRecentGroupSummaries() {
        List<Group> recentGroups = groupRepository.findTop5ByOrderByGroupIdDesc();
        List<GroupSummaryOutDTO> groupSummary = convertToGroupSummaries(recentGroups);
        return StandardResponseOutDTO.success(groupSummary, "Group summary fetched successfully");
    }

    /**
     * Converts a list of Group entities to GroupSummaryOutDTO objects.
     * Calculates member count and retrieves creator information for each group.
     *
     * @param groups the list of Group entities to convert
     * @return list of GroupSummaryOutDTO objects containing group summaries
     * @throws ResourceNotFoundException if any group creator is not found
     */
    private List<GroupSummaryOutDTO> convertToGroupSummaries(final List<Group> groups) {
        return groups.stream()
                .map(group -> {
                    long memberCount = userGroupRepository.findAllByGroupId(group.getGroupId()).size();
                    String creatorName = userRepository.findById(group.getCreatorId())
                            .map(user -> user.getFirstName() + " " + user.getLastName())
                            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

                    return new GroupSummaryOutDTO(
                            group.getGroupId(),
                            group.getGroupName(),
                            creatorName,
                            memberCount
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves detailed course information for a specific group.
     * Calculates enrollment counts and average progress for each course.
     *
     * @param groupId the unique identifier of the group
     * @return StandardResponseOutDTO containing list of course details with statistics
     */
    @Override
    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(final long groupId) {
        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
        Map<Long, GroupCourseOutDTO> mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive()) {
                String courseName = courseMicroserviceClient.getCourseNameById(en.getCourseId()).getBody();
                GroupCourseOutDTO gc = mp.getOrDefault(en.getCourseId(), new GroupCourseOutDTO());
                long totalenrols = gc.getEnrols() + 1;
                double userprogress = courseMicroserviceClient.getCourseProgressWithMeta(
                        en.getUserId(), en.getCourseId()).getCourseCompletionPercentage();
                double progress = ((gc.getProgress() * gc.getEnrols()) + userprogress) / totalenrols;
                gc.setCourseName(courseName);
                gc.setCourseId(en.getCourseId());
                gc.setEnrols(totalenrols);
                gc.setProgress(progress);
                mp.put(en.getCourseId(), gc);
            }
        }

        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
    }

    /**
     * Retrieves detailed user information for a specific group.
     * Calculates enrollment counts and average progress for each user.
     * Includes both enrolled users and group members without enrollments.
     *
     * @param groupId the unique identifier of the group
     * @return StandardResponseOutDTO containing list of user details with statistics
     */
    @Override
    public StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(final long groupId) {
        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
        List<UserGroup> usrgrp = userGroupRepository.findAllByGroupId(groupId);
        Map<Long, GroupUserOutDTO> mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive()) {
                Optional<User> usr = userRepository.findById(en.getUserId());
                GroupUserOutDTO uc = mp.getOrDefault(en.getUserId(), new GroupUserOutDTO());
                long totalenrols = uc.getEnrols() + 1;
                double userprogress = courseMicroserviceClient.getCourseProgressWithMeta(
                        en.getUserId(), en.getCourseId()).getCourseCompletionPercentage();
                double progress = ((uc.getProgress() * uc.getEnrols()) + userprogress) / totalenrols;
                uc.setFirstName(usr.get().getFirstName());
                uc.setLastName(usr.get().getLastName());
                uc.setUserId(en.getUserId());
                uc.setEnrols(totalenrols);
                uc.setProgress(progress);
                mp.put(en.getUserId(), uc);
            }
        }

        for (UserGroup user : usrgrp) {
            if (!mp.containsKey(user.getUserId()) && user.isActive()) {
                Optional<User> usr = userRepository.findById(user.getUserId());
                GroupUserOutDTO uc = new GroupUserOutDTO();
                long totalenrols = 0;
                double progress = 0;
                uc.setFirstName(usr.get().getFirstName());
                uc.setLastName(usr.get().getLastName());
                uc.setUserId(usr.get().getUserId());
                uc.setEnrols(totalenrols);
                uc.setProgress(progress);
                mp.put(user.getUserId(), uc);
            }
        }
        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
    }

    @Override
    public StandardResponseOutDTO<List<UserGroupOutDTO>> getUserGroupDetail(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        List<Enrollment> enrols = enrollmentRepository.findByUserId(user.getUserId());

        Map<Long, UserGroupOutDTO> mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive() && en.getGroupId() != null) {
                Optional<Group> group = groupRepository.findByGroupId(en.getGroupId());
                if (group.isPresent()) {
                    String groupName = group.get().getGroupName();
                    UserGroupOutDTO gc = mp.getOrDefault(en.getGroupId(), new UserGroupOutDTO());
                    long totalenrols = gc.getEnrols() + 1;
                    double userprogress = courseMicroserviceClient.getCourseProgressWithMeta(en.getUserId().longValue(), en.getCourseId().longValue()).getCourseCompletionPercentage();
                    double progress = ((gc.getProgress() * gc.getEnrols()) + userprogress) / totalenrols;
                    gc.setGroupName(groupName);
                    gc.setGroupId(group.get().getGroupId());
                    mp.put(en.getCourseId(), gc);
                } else {
                    throw new ResourceNotFoundException(GROUP_NOT_FOUND);
                }
            }
        }
        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
    }

    @Override
    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseEmpDetail(long groupId) {

        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);

        Map<Long, GroupCourseOutDTO> mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive()) {
                String courseName = courseMicroserviceClient.getCourseNameById(en.getCourseId()).getBody();
                GroupCourseOutDTO gc = mp.getOrDefault(en.getCourseId(), new GroupCourseOutDTO());
                gc.setCourseName(courseName);
                gc.setCourseId(en.getCourseId());
                mp.put(en.getCourseId(), gc);
            }
        }


        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
    }
}
