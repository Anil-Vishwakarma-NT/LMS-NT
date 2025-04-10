package com.nt.LMS.service;

import com.nt.LMS.converter.GroupDTOConverter;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.GroupRepository;
import com.nt.LMS.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.nt.LMS.constants.UserConstants.UPDATED;
import static com.nt.LMS.constants.UserConstants.USER_NOT_FOUND;

@Service
@Slf4j // Add the @Slf4j annotation to enable logging
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOConverter userDTOConverter;

    @Autowired
    private GroupDTOConverter groupDTOConverter;

    public void createGroup(String group_name , String username){
        try {
            log.info("Attempting to create a group with name: {} by user: {}", group_name, username);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Group group = new Group(group_name, user.getUserId());
            groupRepository.save(group);
            log.info("Group '{}' created successfully with creator '{}'", group_name, username);
        } catch (Exception e) {
            log.error("Error while creating group '{}' by user '{}'", group_name, username, e);
            throw new RuntimeException(e);
        }
    }

    public String delGroup(long group_id){
        try {
            log.info("Attempting to delete group with ID: {}", group_id);
            Group group = groupRepository.findById(group_id);
            // Step 2: Remove this group from each associated user's group set
            Set<User> users = group.getUsers();
            for (User user : users) {
                user.getGroups().remove(group); // Remove group from user
                userRepository.save(user);      // Persist the change
            }
            // Step 3: Clear the users from group to break the link from the group side
            group.getUsers().clear();
            // Step 4: Delete the group
            groupRepository.delete(group);
            log.info("Group with ID: {} deleted successfully", group_id);
            return "Group deleted successfully";
        }  catch (Exception e) {
            log.error("Error while deleting group with ID: {}", group_id, e);
            throw new RuntimeException(e);
        }
    }

    public String addUserToGroup(long userId, long groupId) {
        try {
            log.info("Attempting to add user with ID: {} to group with ID: {}", userId, groupId);
            Group group = groupRepository.findById(groupId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

            // Ensure bidirectional update
            group.getUsers().add(user);
            user.getGroups().add(group); // ← ADD THIS LINE

            userRepository.save(user); // Save user to persist both sides
            log.info("User with ID: {} successfully added to group with ID: {}", userId, groupId);
            return "User successfully added to group.";
        } catch (Exception e) {
            log.error("Error while adding user with ID: {} to group with ID: {}", userId, groupId, e);
            throw new RuntimeException("Error while adding user to group", e);
        }
    }

    public String removeUserFromGroup(long userId , long group_id){
        try {
            log.info("Attempting to remove user with ID: {} from group with ID: {}", userId, group_id);
            Group group = groupRepository.findById(group_id);
            Optional<User> userOpt = userRepository.findById(userId);

            User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
            Set<User> users = group.getUsers();
            if (users.contains(user)) {
                // Step 4: Remove the user from the group
                users.remove(user);
                group.setUsers(users); // Update the group side of the relationship

                // Step 5: Remove the group from the user's set of groups
                user.getGroups().remove(group);

                // Step 6: Save the changes
                groupRepository.save(group); // Save the updated group
                userRepository.save(user);   // Save the updated user
                log.info("User with ID: {} successfully removed from group with ID: {}", userId, group_id);
                return "User removed from the group successfully";
            } else {
                log.warn("User with ID: {} is not part of the group with ID: {}", userId, group_id);
                throw new ResourceNotFoundException("User is not part of the group.");
            }
        } catch (Exception e) {
            log.error("Error while removing user with ID: {} from group with ID: {}", userId, group_id, e);
            throw new RuntimeException(e);
        }
    }

    public List<UserOutDTO> getUsersInGroup(long group_id){
        try {
            log.info("Fetching users in group with ID: {}", group_id);
            Group group = groupRepository.findById(group_id);
            Set<User> users = group.getUsers();
            List<UserOutDTO> response = new ArrayList<>();
            for(User u : users){
                String managername = "";
                UserOutDTO userdto = userDTOConverter.userToOutDto(u , managername);
                response.add(userdto);
            }
            log.info("Fetched {} users in group with ID: {}", response.size(), group_id);
            return response;
        } catch (Exception e) {
            log.error("Error while fetching users in group with ID: {}", group_id, e);
            throw new RuntimeException(e);
        }
    }

    public List<GroupOutDTO> getGroups(String email){
        try {
            log.info("Fetching groups for user with email: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
            List<Group> groups = groupRepository.getGroups(user.getUserId());
            List<GroupOutDTO> groupout = new ArrayList<>();
            for(Group group : groups){
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group , user.getFirstName() + user.getLastName());
                groupout.add(gout);
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
