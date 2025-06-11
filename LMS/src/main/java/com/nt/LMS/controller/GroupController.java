package com.nt.LMS.controller;

import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.serviceImpl.GroupServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling group-related operations.
 */
@RestController
@RequestMapping("/group")
@Slf4j
public final class GroupController {

    /** Service class to handle group logic. */
    @Autowired
    private GroupServiceImpl groupService;

    /** Repository for accessing user data. */
    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new group.
     *
     * @param groupInDTO Group creation details.
     * @return success message.
     */
    @PostMapping("/create-group")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> createGroup(@Valid @RequestBody final GroupInDTO groupInDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Attempting to create a group with name: {}", groupInDTO.getGroupName());
        StandardResponseOutDTO<MessageOutDto> response = groupService.createGroup(groupInDTO.getGroupName(), username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a group by ID.
     *
     * @param groupId group ID.
     * @return success message.
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> deleteGroup(@PathVariable final long groupId) {
        log.info("Attempting to delete group with ID: {}", groupId);
        StandardResponseOutDTO<MessageOutDto> response = groupService.deleteGroup(groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Adds a user to a group.
     *
     * @param groupInDTO DTO containing user and group IDs.
     * @return success message.
     */
    @PostMapping("/add-user")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> addUserToGroup(@Valid @RequestBody final GroupInDTO groupInDTO) {
        log.info("Attempting to add user with ID: {} to group with ID: {}", groupInDTO.getUserId(), groupInDTO.getGroupId());
        StandardResponseOutDTO<MessageOutDto> response = groupService.addUserToGroup(groupInDTO.getUserId(), groupInDTO.getGroupId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Removes a user from a group.
     *
     * @param groupdto DTO containing user and group IDs.
     * @return success message.
     */
    @DeleteMapping("/remove-user")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> removeUserFromGroup(@Valid @RequestBody final GroupInDTO groupdto) {
        log.info("Attempting to remove user with ID: {} from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());
        StandardResponseOutDTO<MessageOutDto> response = groupService.removeUserFromGroup(groupdto.getUserId(), groupdto.getGroupId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets the list of users in a group.
     *
     * @param groupId the group ID.
     * @return list of users.
     */
    @GetMapping("/group-emps/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserOutDTO>>> getUsersInGroup(@PathVariable final long groupId) {
        log.info("Fetching users in group with ID: {}", groupId);
        StandardResponseOutDTO<List<UserOutDTO>> response = groupService.getUsersInGroup(groupId);

        if (response.getData().isEmpty()) {
            log.warn("No users found in group with ID: {}", groupId);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Users found in group with ID: {}: {}", groupId, response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves groups for the current user.
     *
     * @return list of groups.
     */
    @GetMapping("/groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Fetching groups for user: {}", username);
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getGroups(username);

        if (response.getData().isEmpty()) {
            log.warn("No groups found for user: {}", username);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups for user: {}", response.getData().size(), username);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Retrieves all groups (admin only).
     *
     * @return list of all groups.
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/all-groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getAllGroups() {
        log.info("Fetching all groups (Admin access required)");
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getAllGroups();
        log.info("Fetched {} groups", response.getData().size());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/count")
    public ResponseEntity<MessageOutDto> getGroupCount() {
        log.info("Received request to get total Group count.");
        long count = groupService.countGroups();
        log.info("Total Group count retrieved: {}", count);
        MessageOutDto message =new MessageOutDto();
        message.setMessage(""+count);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<GroupSummaryDTO>>> getRecentGroups() {
        StandardResponseOutDTO<List<GroupSummaryDTO>> response = groupService.getRecentGroupSummaries();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
