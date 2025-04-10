package com.nt.LMS.controller;

import com.nt.LMS.dto.GroupInDTO;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
//import com.nt.LMS.exceptions.GroupNotFoundException;
//import com.nt.LMS.exceptions.InvalidGroupException;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;


    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestBody @Valid GroupInDTO groupInDTO) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            groupService.createGroup(groupInDTO.getGroupName(), username);
            return ResponseEntity.status(HttpStatus.CREATED).body("Group successfully created");
        }


    @DeleteMapping("/del-group/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable long groupId) {
            return ResponseEntity.ok(groupService.delGroup(groupId));
    }


    @PostMapping("/add-user")
    public ResponseEntity<String> addUserToGroup(@RequestBody GroupInDTO groupInDTO) {
            return ResponseEntity.ok(groupService.addUserToGroup(groupInDTO.getUserId(), groupInDTO.getGroupId()));
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity<String> removeUserFromGroup(@RequestParam GroupInDTO groupdto) {
            return ResponseEntity.ok(groupService.removeUserFromGroup(groupdto.getUserId(), groupdto.getGroupId()));
    }


    @GetMapping("/group-emps/{groupId}")
    public ResponseEntity<List<UserOutDTO>> getUsersInGroup(@PathVariable long groupId) {
            List<UserOutDTO> users = groupService.getUsersInGroup(groupId);
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();  // HTTP 204 No Content if no users
            }
            return ResponseEntity.ok(users); // HTTP 200 OK
    }


//review again
    @GetMapping("/groups")
    public ResponseEntity<List<GroupOutDTO>> getGroups(@RequestBody GroupInDTO groupdto){
            List<GroupOutDTO> groups = groupService.getGroups(groupdto.getUserId());
                return ResponseEntity.ok(groups);

    }

    @GetMapping("/all-groups")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<GroupOutDTO>> getAllGroups(){
             List<GroupOutDTO> groups = groupService.getAllGroups();
            return ResponseEntity.ok(groups);
    }
    //

}
