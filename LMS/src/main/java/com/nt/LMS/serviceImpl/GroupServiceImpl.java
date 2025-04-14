package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.GroupDTOConverter;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;
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
import org.springframework.stereotype.Service;

import java.util.*;

import static com.nt.LMS.constants.GroupConstants.*;
import static com.nt.LMS.constants.UserConstants.*;

@Service
@Slf4j // Add the @Slf4j annotation to enable logging
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOConverter userDTOConverter;

    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private GroupDTOConverter groupDTOConverter;

    public MessageOutDto createGroup(String groupName , String username){
        try {
            log.info("Attempting to create a group with name: {} by user: {}", groupName, username);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));
            Group group = new Group(groupName, user.getUserId());
            groupRepository.save(group);
            log.info("Group '{}' created successfully with creator '{}'", groupName, username);
            return new MessageOutDto(GROUP_CREATED);
        } catch (Exception e) {
            log.error("Error while creating group '{}' by user '{}'", groupName, username, e);
            throw new RuntimeException(e);
        }
    }


    public MessageOutDto delGroup(long groupId) {
        try {
            log.info("Attempting to delete group with ID: {}", groupId);

            Group group = groupRepository.findById(groupId).orElseThrow(() -> {
                        log.warn("Group not found with ID {}", groupId);
                        return new ResourceNotFoundException(GROUP_NOT_FOUND); });

            userGroupRepository.deleteByGroupId(groupId);
            groupRepository.delete(group);

            log.info("Group with ID: {} deleted successfully", groupId);

            return new MessageOutDto(GROUP_DELETED);

        } catch (Exception e) {

            log.error("Error while deleting group with ID: {}", groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);

        }
    }


    public MessageOutDto addUserToGroup(long userId, long groupId) {
        try {
            log.info("Attempting to add user with ID: {} to group with ID: {}", userId, groupId);

            if(groupRepository.findById(groupId).isEmpty()){
                log.warn(GROUP_NOT_FOUND);
                throw new ResourceNotFoundException(GROUP_NOT_FOUND);
            }
            if(userRepository.findById(userId).isEmpty()){
                log.warn(USER_NOT_FOUND);
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            if(userGroupRepository.findByUserIdAndGroupId(userId,groupId).isPresent()){
                log.info(USER_ALREADY_PRESENT_IN_GROUP);
                return new MessageOutDto(USER_ALREADY_PRESENT_IN_GROUP);
            }
            UserGroup userGroup = new UserGroup(userId,groupId) ;
            userGroupRepository.save(userGroup);

            log.info("User with ID: {} successfully added to group with ID: {}", userId, groupId);

            return new MessageOutDto(USER_ADDED_TO_GROUP);
        } catch (Exception e) {
            log.error("Error while adding user with ID: {} to group with ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }


    public MessageOutDto removeUserFromGroup(long userId, long groupId) {
        try {
            log.info("Attempting to remove user with ID: {} from group with ID: {}", userId, groupId);

            UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId).orElseThrow(()->{
                        log.warn(USER_NOT_FOUND_IN_GROUP);
                        throw new ResourceNotFoundException(USER_NOT_FOUND_IN_GROUP);    } );

            userGroupRepository.delete(userGroup);

            log.info("User with ID: {} successfully removed from group with ID: {}", userId, groupId);

            return new MessageOutDto(User_REMOVED_SUCCESSFULLY);

        } catch (Exception e) {
            log.error("Error while removing user with ID: {} from group with ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }


    public List<UserOutDTO> getUsersInGroup(long groupId) {
        try {
            log.info("Fetching users in group with ID: {}", groupId);
            Group group= groupRepository.findById(groupId).orElseThrow(()->{
                        log.warn("Group not found" + groupId);
                        return new ResourceNotFoundException(GROUP_NOT_FOUND);   });

            List<UserGroup> userGroupList = userGroupRepository.findAllByGroupId(groupId);

            if (userGroupList.isEmpty()) {
                log.info("No users found in group with ID: {}", groupId);
                return Collections.emptyList();
            }

            List<UserOutDTO> response = new ArrayList<>();

            for (UserGroup ug : userGroupList) {
                User user = userRepository.findById(ug.getUserId()).orElseThrow(()->{
                    log.warn(USER_NOT_FOUND);
                    throw new ResourceNotFoundException(USER_NOT_FOUND);
                });
                    User manager = userRepository.findById(user.getManagerId()).orElseGet(() -> {
                        log.error("Manager with ID {} not found", user.getManagerId());
                        User fallbackManager = new User();
                        fallbackManager.setFirstName("Unknown");
                        fallbackManager.setLastName(" ");
                        return fallbackManager;
                    });
                    UserOutDTO dto = userDTOConverter.userToOutDto(user, manager.getFirstName()+manager.getLastName());
                    response.add(dto);
            }
            log.info("Fetched {} users in group with ID: {}", response.size(), groupId);
            return response;
        } catch (Exception e) {
            log.error("Error while fetching users in group with ID: {}", groupId, e);
            throw new RuntimeException(GROUP_FAILURE + groupId, e);
        }
    }


    public List<GroupOutDTO> getGroups(String email){
        try {
            log.info("Fetching groups for user with email: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
            List<Group> groups = groupRepository.findByCreatorId(user.getUserId());

            List<GroupOutDTO> groupout = new ArrayList<>();

            for(Group group : groups){
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group , user.getFirstName() + user.getLastName());
                groupout.add(gout);
            }

            if(user.getUserId() != ADMIN_ID) {
                List<Group> adminGroups = groupRepository.findByCreatorId(ADMIN_ID);
                for (Group group : adminGroups) {
                    GroupOutDTO gout = groupDTOConverter.groupToOutDto(group, user.getFirstName() + user.getLastName());
                    groupout.add(gout);
                }
            }

            log.info("Fetched {} groups for user with email: {}", groupout.size(), email);
            return groupout;
        } catch (Exception e) {
            log.error("Error while fetching groups for user with email: {}", email, e);
            throw new RuntimeException(e);
        }
    }

    public List<GroupOutDTO> getAllGroups(){
        try {
            log.info("Fetching all groups");
            List<Group> groups = groupRepository.findAll();
            List<GroupOutDTO> groupout = new ArrayList<>();
            for(Group group : groups){
                Optional<User> creatorOp = userRepository.findById(group.getCreatorId());
                User creator = creatorOp.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group , creator.getFirstName() + creator.getLastName());
                groupout.add(gout);
            }
            log.info("Fetched {} groups", groupout.size());
            return groupout;
        } catch (Exception e) {
            log.error("Error while fetching all groups", e);
            throw new RuntimeException(e);
        }
    }
}
