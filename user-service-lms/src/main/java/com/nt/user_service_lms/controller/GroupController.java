package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.EnrollmentsService;
import com.nt.user_service_lms.service.GroupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling group-related operations.
 */
@RestController
@RequestMapping("/api/service-api/group")
@Slf4j
public class GroupController {

    /** Service class to handle group logic. */
    @Autowired
    private GroupService groupService;

    /** Repository for accessing user data. */
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EnrollmentsService enrollmentsService;
    /**
     * Creates a new group.
     *
     * @param groupInDTO Group creation details.
     * @return success message.
     */
    @PostMapping("/create-group")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> createGroup(@Valid @RequestBody final GroupInDTO groupInDTO) {  // alag alag dto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String username = principal.getUserEmail();
        log.info("Attempting to create a group with name: {}", groupInDTO.getGroupName());
        StandardResponseOutDTO<MessageOutDto> response = groupService.createGroup(groupInDTO.getGroupName(), username ,groupInDTO.getEmployees());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a group by ID.
     *
     * @param groupId group ID.
     * @return success message.
     */
    @DeleteMapping("/remove/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> deleteGroup(@PathVariable final long groupId) {
        log.info("Attempting to delete group with ID: {}", groupId);
        StandardResponseOutDTO<MessageOutDto> response = groupService.deleteGroup(groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /**
     * Updates a group.
     *
     * @param groupInDTO DTO containing user and group IDs.
     * @return success message.
     */
    @PutMapping("/update-group")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> updateGroup(@RequestBody final GroupInDTO groupInDTO){
        log.info("Updating Group details");
        StandardResponseOutDTO<MessageOutDto> response = groupService.updateGroup(groupInDTO.getGroupId(),groupInDTO.getGroupName());
        return new ResponseEntity<>(response , HttpStatus.OK);
    }



    /**
     * Adds a user to a group.
     *
     * @param groupInDTO DTO containing user and group IDs.
     * @return success message.
     */
    @PostMapping("/add-user")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDto>> addUserToGroup(@Valid @RequestBody  GroupInDTO groupInDTO) {
        log.info("Attempting to add user with ID: to group with ID: {}", groupInDTO.getGroupId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String username = principal.getUserEmail();
        StandardResponseOutDTO<MessageOutDto> response = groupService.addUserToGroup(groupInDTO ,username );
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
    public ResponseEntity<StandardResponseOutDTO<List<GroupUserOutDTO>>> getUsersInGroup(@PathVariable final long groupId) {
        log.info("Fetching users in group with ID: {}", groupId);
        StandardResponseOutDTO<List<GroupUserOutDTO>> response = groupService.getUserDetail(groupId);

        if (response.getData().isEmpty()) {
            log.warn("No users found in group with ID: {}", groupId);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Users found in group with ID: {}: {}", groupId, response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("emp/group-emps/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<List<GroupUserOutDTO>>> getUsersInGroupForEmp(@PathVariable final long groupId) {
        log.info("Fetching users in group with ID: {}", groupId);
        StandardResponseOutDTO<List<GroupUserOutDTO>> response = groupService.getUserDetailEmp(groupId);

        if (response.getData().isEmpty()) {
            log.warn("No users found in group with ID: {}", groupId);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Users found in group with ID: {}: {}", groupId, response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/group-courses/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<List<GroupCourseOutDTO>>> getCourseDetails(@PathVariable final long groupId){     // pass group id in dto
        log.info("Attempting to get course details of groupId : {}",groupId);
        StandardResponseOutDTO<List<GroupCourseOutDTO>> response = groupService.getCourseDetail(groupId);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("emp/group-courses/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<List<GroupCourseOutDTO>>> getCourseEmpDetails(@PathVariable final long groupId){     // pass group id in dto
        log.info("Attempting to get course details of groupId : {}",groupId);
        StandardResponseOutDTO<List<GroupCourseOutDTO>> response = groupService.getCourseEmpDetail(groupId);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    /**
     * Retrieves groups for the current user.
     *
     * @return list of groups.
     */
    @GetMapping("/groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getGroups() {                  // change group/groups to group
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String email = principal.getUserEmail();
        log.info("Fetching groups for user: {}", email);
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getGroups(email);

        if (response.getData().isEmpty()) {
            log.warn("No groups found for user: {}", email);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups for user: {}", response.getData().size(), email);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/Allgroups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getAllGroups() {                  // change group/groups to group

        log.info("Fetching groups ");
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getAllGroups();


        if (response.getData().isEmpty()) {
            log.warn("No groups found ");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups ", response.getData().size());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/all-active-groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getAllActiveGroups() {                  // change group/groups to group

        log.info("Fetching groups ");
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getAllActiveGroups();


        if (response.getData().isEmpty()) {
            log.warn("No groups found ");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups ", response.getData().size());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<StandardResponseOutDTO<Long>> getGroupCount() {
        log.info("Received request to get total Group count.");
        long count = groupService.countGroups();
        log.info("Total Group count retrieved: {}", count);

        StandardResponseOutDTO<Long> response = new StandardResponseOutDTO<>();
        response.setData(count);
        response.setMessage("Group count retrieved successfully.");
        response.setStatus("success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<GroupSummaryOutDTO>>> getRecentGroups() {
        StandardResponseOutDTO<List<GroupSummaryOutDTO>> response = groupService.getRecentGroupSummaries();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/user-courses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getUserCoursesInGroups(@RequestBody GroupInDTO groupInDTO){
        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = groupService.getUserCourses(groupInDTO.getGroupId(), groupInDTO.getUserId());
        if(response.getData().isEmpty()){
            return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/user-groups")
    public ResponseEntity<StandardResponseOutDTO<List<UserGroupOutDTO>>>getUserGroupDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String username = principal.getUserEmail();
        StandardResponseOutDTO<List<UserGroupOutDTO>> response = groupService.getUserGroupDetail(username);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

}
