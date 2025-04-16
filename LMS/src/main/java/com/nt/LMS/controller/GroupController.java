package com.nt.LMS.controller;

import com.nt.LMS.dto.GroupInDTO;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
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
    private GroupServiceImpl groupService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-group")
    public ResponseEntity<MessageOutDto> createGroup(@RequestBody @Valid GroupInDTO groupInDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Attempting to create a group with name: {}", groupInDTO.getGroupName());
        log.info("Group successfully created by user: {}", username);
        return new ResponseEntity<>(groupService.createGroup(groupInDTO.getGroupName(), username), HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<MessageOutDto> deleteGroup(@PathVariable long groupId) {
        log.info("Attempting to delete group with ID: {}", groupId);
        log.info("Group with ID: {} deleted successfully", groupId);
        return new ResponseEntity<>(groupService.delGroup(groupId), HttpStatus.OK);
    }

    @PostMapping("/add-user")
    public ResponseEntity<MessageOutDto> addUserToGroup(@RequestBody @Valid GroupInDTO groupInDTO) {
        log.info("Attempting to add user with ID: {} to group with ID: {}", groupInDTO.getUserId(), groupInDTO.getGroupId());
        log.info("User with ID: {} added to group with ID: {}", groupInDTO.getUserId(), groupInDTO.getGroupId());
        return new ResponseEntity<>(groupService.addUserToGroup(groupInDTO.getUserId(), groupInDTO.getGroupId()),HttpStatus.OK);
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity<MessageOutDto> removeUserFromGroup(@RequestBody @Valid GroupInDTO groupdto) {
        log.info("Attempting to remove user with ID: {} from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());
        log.info("User with ID: {} removed from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());
        return new ResponseEntity<>(groupService.removeUserFromGroup(groupdto.getUserId(), groupdto.getGroupId()),HttpStatus.OK);
    }

    @GetMapping("/group-emps/{groupId}")
    public ResponseEntity<List<UserOutDTO>> getUsersInGroup(@PathVariable long groupId) {
        log.info("Fetching users in group with ID: {}", groupId);

        List<UserOutDTO> users = groupService.getUsersInGroup(groupId);

        if (users.isEmpty()) {
            log.warn("No users found in group with ID: {}", groupId);
            return new ResponseEntity<>(users,HttpStatus.NO_CONTENT);
        }

        log.info("Users found in group with ID: {}: {}", groupId, users.size());
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupOutDTO>> getGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Fetching groups for user: {}", username);
        List<GroupOutDTO> groups = groupService.getGroups(username);
        if(groups.isEmpty()){
            log.warn("No groups found");
            return new  ResponseEntity<>(groups,HttpStatus.NO_CONTENT);
        }
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
