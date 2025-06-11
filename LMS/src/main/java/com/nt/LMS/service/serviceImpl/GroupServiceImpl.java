package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.constants.GroupConstants;
import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.converter.GroupDTOConverter;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.*;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserGroup;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.UnauthorizedAccessException;
import com.nt.LMS.repository.GroupRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.LMS.constants.GroupConstants.GROUP_CREATED;
import static com.nt.LMS.constants.GroupConstants.GROUP_DELETED;
import static com.nt.LMS.constants.GroupConstants.GROUP_FAILURE;
import static com.nt.LMS.constants.GroupConstants.GROUP_NOT_FOUND;
import static com.nt.LMS.constants.GroupConstants.USER_ADDED_TO_GROUP;
import static com.nt.LMS.constants.GroupConstants.USER_ALREADY_PRESENT_IN_GROUP;
import static com.nt.LMS.constants.UserConstants.USER_NOT_FOUND;
import static com.nt.LMS.constants.GroupConstants.USER_NOT_FOUND_IN_GROUP;
import static com.nt.LMS.constants.GroupConstants.USER_REMOVED_SUCCESSFULLY;

/**
 * Implementation of the GroupService interface for managing user groups.
 */
@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    /**
     * To use group services.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * To use user services.
     */
    @Autowired
    private UserRepository userRepository;


    /**
     * To convert user to dto.
     */
    @Autowired
    private UserDTOConverter userDTOConverter;

    /**
     * To use repo services.
     */
    @Autowired
    private UserGroupRepository userGroupRepository;


    /**
     * To convert group to dto.
     */
    @Autowired
    private GroupDTOConverter groupDTOConverter;

    /**
     * Creates a new group.
     *
     * @param groupName the name of the group
     * @param username  the creator's email
     * @return a success message
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> createGroup(final String groupName, final String username) {
        try {
            log.info("Attempting to create a group with name: {} by user: {}", groupName, username);
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));

            Group group = new Group(groupName, user.getUserId());
            groupRepository.save(group);
            log.info("Group '{}' created successfully by '{}'", groupName, username);

            MessageOutDto messageOutDto = new MessageOutDto(GROUP_CREATED);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error while creating group '{}' by '{}'", groupName, username, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a group by its ID.
     *
     * @param groupId the group ID
     * @return a success message
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> deleteGroup(final long groupId) {
        try {
            log.info("Attempting to delete group with ID: {}", groupId);
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

            userGroupRepository.deleteByGroupId(groupId);
            groupRepository.delete(group);
            log.info("Group with ID: {} deleted successfully", groupId);

            MessageOutDto messageOutDto = new MessageOutDto(GROUP_DELETED);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error while deleting group with ID: {}", groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Adds a user to a group.
     *
     * @param userId  the user ID
     * @param groupId the group ID
     * @return a success or failure message
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> addUserToGroup(final long userId, final long groupId) {
        try {
            log.info("Adding user ID: {} to group ID: {}", userId, groupId);

            if (groupRepository.findById(groupId).isEmpty()) {
                throw new ResourceNotFoundException(GROUP_NOT_FOUND);
            }

            if (userRepository.findById(userId).isEmpty()) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }

            if (userGroupRepository.findByUserIdAndGroupId(userId, groupId).isPresent()) {
                MessageOutDto messageOutDto = new MessageOutDto(USER_ALREADY_PRESENT_IN_GROUP);
                return StandardResponseOutDTO.success(messageOutDto,null);
            }

            UserGroup userGroup = new UserGroup(userId, groupId);
            userGroupRepository.save(userGroup);

            MessageOutDto messageOutDto =  new MessageOutDto(USER_ADDED_TO_GROUP);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error adding user ID: {} to group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Removes a user from a group.
     *
     * @param userId  the user ID
     * @param groupId the group ID
     * @return a success message
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> removeUserFromGroup(final long userId, final long groupId) {
        try {
            UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_IN_GROUP));

            userGroupRepository.delete(userGroup);
            MessageOutDto messageOutDto = new MessageOutDto(USER_REMOVED_SUCCESSFULLY);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error removing user ID: {} from group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    /**
     * Gets all users in a group.
     *
     * @param groupId the group ID
     * @return list of users
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getUsersInGroup(final long groupId) {
        try {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));

            List<UserGroup> userGroupList = userGroupRepository.findAllByGroupId(groupId);
            if (userGroupList.isEmpty()) {
                return StandardResponseOutDTO.success(Collections.emptyList(), USER_NOT_FOUND_IN_GROUP);
            }

            List<UserOutDTO> response = new ArrayList<>();
            for (UserGroup ug : userGroupList) {
                User user = userRepository.findById(ug.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

                User manager = userRepository.findById(user.getManagerId())
                        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

                UserOutDTO dto = userDTOConverter.userToOutDto(user,
                        manager.getFirstName() + " " + manager.getLastName(),"employee");
                response.add(dto);
            }

            return StandardResponseOutDTO.success(response,"Group Employee fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching users in group ID: {}", groupId, e);
            throw new RuntimeException(GROUP_FAILURE + groupId, e);
        }
    }

    /**
     * Gets groups created by a user or assigned by admin.
     *
     * @param email the user's email
     * @return list of groups
     */
    @Override
    public StandardResponseOutDTO<List<GroupOutDTO>> getGroups(final String email) {
        try {
            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

            List<GroupOutDTO> groupOutList = new ArrayList<>();
            List<Group> userGroups = groupRepository.findByCreatorId(user.getUserId());

            for (Group group : userGroups) {
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                        user.getFirstName() + " " + user.getLastName());
                groupOutList.add(gout);
            }

            if (user.getUserId() != UserConstants.getAdminId()) {
                List<Group> adminGroups = groupRepository.findByCreatorId(UserConstants.getAdminId());
                for (Group group : adminGroups) {
                    GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                            user.getFirstName() + " " + user.getLastName());
                    groupOutList.add(gout);
                }
            }

            return StandardResponseOutDTO.success(groupOutList,"Group fetched Successfully");
        } catch (Exception e) {
            log.error("Error fetching groups for user with email: {}", email, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all groups in the system.
     *
     * @return list of all groups
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

            return StandardResponseOutDTO.success(groupOutList,"All Groups fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all groups", e);
            throw new RuntimeException(e);
        }
    }
    @Override
    public long countGroups() {

        return groupRepository.count();
    }


    @Override
    public StandardResponseOutDTO<List<GroupSummaryDTO>> getRecentGroupSummaries() {
        // Get the 5 most recent groups
        List<Group> recentGroups = groupRepository.findTop5ByOrderByGroupIdDesc();
        List<GroupSummaryDTO> groupSummary =  convertToGroupSummaries(recentGroups);
        return StandardResponseOutDTO.success(groupSummary, "Group summary feched successfully");
    }

    /**
     * Converts a list of Group entities to GroupSummaryDTOs with member counts using streams.
     *
     * @param groups the list of groups to convert
     * @return a list of GroupSummaryDTOs
     */
    private List<GroupSummaryDTO> convertToGroupSummaries(List<Group> groups) {
        return groups.stream()
                .map(group -> {
                    // Get member count
                    long memberCount = userGroupRepository.findAllByGroupId(group.getGroupId()).size();

                    // Get creator name - assuming you have a method to get user by ID
                    String creatorName = userRepository.findById(group.getCreatorId())
                            .map(user -> user.getFirstName() + " " + user.getLastName())
                            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

                    return new GroupSummaryDTO(
                            group.getGroupId(),
                            group.getGroupName(),
                            creatorName,
                            memberCount
                    );
                })
                .collect(Collectors.toList());
    }
}
