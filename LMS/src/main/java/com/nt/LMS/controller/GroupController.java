package com.nt.LMS.controller;


import com.nt.LMS.dto.Admindto;
import com.nt.LMS.dto.GroupDTO;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.exceptions.GroupNotFoundException;
import com.nt.LMS.exceptions.InvalidGroupException;
import com.nt.LMS.exceptions.UserNotFoundException;
import com.nt.LMS.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;


    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestBody @Valid GroupDTO groupDTO, BindingResult bindingResult) {
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body("Validation failed: " + errorMessages);
        }

        try {
            groupService.createGroup(groupDTO.getGroupName(), groupDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Group successfully created");
        } catch (InvalidGroupException e) {
            return ResponseEntity.badRequest().body("Invalid group data: " + e.getMessage());
        } catch (Exception e) {
            // General exception handler
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the group");
        }
    }

    @DeleteMapping("/del-group/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable long groupId) {
        try {
            groupService.delGroup(groupId);
            return ResponseEntity.ok("Group deleted successfully.");
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting group.");
        }
    }
    //avoid Request Param
//    @PostMapping("/add-user")
//    public ResponseEntity<String> addUserToGroup(@RequestBody GroupDTO groupDTO) {
//        try {
//            groupService.addUserToGroup(groupDTO.getUserId(), groupDTO.getGroupId());
//            return ResponseEntity.ok("User added to group.");
//        } catch (GroupNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user to group.");
//        }
//    }

//    @DeleteMapping("/remove-user")
//    public ResponseEntity<String> removeUserFromGroup(@RequestParam long group_id, @RequestParam long user_id) {
//        try {
//            groupService.removeUserInGroup(user_id, group_id);
//            return ResponseEntity.ok("User removed successfully.");
//        } catch (GroupNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing user from group.");
//        }
//    }

    @GetMapping("/group-emps/{groupId}")
    public ResponseEntity<List<Admindto>> getUsersInGroup(@PathVariable long groupId) {
        try {
            Set<User> users = groupService.getUsersInGroup(groupId);
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();  // HTTP 204 No Content if no users
            }
            List<Admindto> admindtos = users.stream()
                    .map(Admindto::new)  // Using the constructor to convert User to Admindto
                    .collect(Collectors.toList());
            return ResponseEntity.ok(admindtos);  // HTTP 200 OK
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // HTTP 404 Not Found if group doesn't exist
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // HTTP 500 Internal Server Error for any other issue
        }
    }


    @GetMapping("/get-groups")
    public ResponseEntity<List<GroupDTO>> getGroups(@RequestBody GroupDTO groupdto){

        try{
            List<Group> groups = groupService.getGroups(groupdto.getUserId());

        List<GroupDTO> group = groups.stream()
                .map(GroupDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(group);
    }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get-all-groups")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<GroupDTO>> getAllGroups(){
        try{
             List<Group> groups = groupService.getAllGroups();
            List<GroupDTO> group = groups.stream()
                    .map(GroupDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(group);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
