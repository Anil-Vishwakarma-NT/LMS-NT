package com.nt.LMS.controller;

import com.nt.LMS.dto.GroupInDTO;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.service.GroupService;
import com.nt.LMS.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/group")
@Slf4j  // Add the @Slf4j annotation to enable logging
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestBody @Valid GroupInDTO groupInDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Attempting to create a group with name: {}", groupInDTO.getGroupName());
        groupService.createGroup(groupInDTO.getGroupName(), username);

        log.info("Group successfully created by user: {}", username);
        return ResponseEntity.status(HttpStatus.CREATED).body("Group successfully created");
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable long groupId) {
        log.info("Attempting to delete group with ID: {}", groupId);

        String response = groupService.delGroup(groupId);

        log.info("Group with ID: {} deleted successfully", groupId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> addUserToGroup(@RequestBody GroupInDTO groupInDTO) {
        log.info("Attempting to add user with ID: {} to group with ID: {}", groupInDTO.getUserId(), groupInDTO.getGroupId());

        String response = groupService.addUserToGroup(groupInDTO.getUserId(), groupInDTO.getGroupId());

        log.info("User with ID: {} added to group with ID: {}", groupInDTO.getUserId(), groupInDTO.getGroupId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity<String> removeUserFromGroup(@RequestBody GroupInDTO groupdto) {
        log.info("Attempting to remove user with ID: {} from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());

        String response = groupService.removeUserFromGroup(groupdto.getUserId(), groupdto.getGroupId());

        log.info("User with ID: {} removed from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group-emps/{groupId}")
    public ResponseEntity<List<UserOutDTO>> getUsersInGroup(@PathVariable long groupId) {
        log.info("Fetching users in group with ID: {}", groupId);

        List<UserOutDTO> users = groupService.getUsersInGroup(groupId);

        if (users.isEmpty()) {
            log.warn("No users found in group with ID: {}", groupId);
            return ResponseEntity.noContent().build();
        }

        log.info("Users found in group with ID: {}: {}", groupId, users.size());
        return ResponseEntity.ok(users); // HTTP 200 OK
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupOutDTO>> getGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Fetching groups for user: {}", username);
        List<GroupOutDTO> groups = groupService.getGroups(username);

        log.info("Found {} groups for user: {}", groups.size(), username);
        return ResponseEntity.ok(groups);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/all-groups")
    public ResponseEntity<List<GroupOutDTO>> getAllGroups() {
        log.info("Fetching all groups (Admin access required)");

        List<GroupOutDTO> groups = groupService.getAllGroups();

        log.info("Fetched {} groups", groups.size());
        return ResponseEntity.ok(groups);
    }
}
