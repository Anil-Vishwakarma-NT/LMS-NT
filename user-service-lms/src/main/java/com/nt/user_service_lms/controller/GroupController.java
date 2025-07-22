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
 * REST Controller for handling group-related operations in the service API.
 *
 * This controller provides endpoints for managing groups, including creating, updating,
 * deleting groups, managing group membership, and retrieving group information.
 * All endpoints are secured and require proper authentication.
 *
 * @author System
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/service-api/group")
@Slf4j
public class GroupController {

    /**
     * Service class to handle group business logic operations.
     * Injected via Spring's dependency injection mechanism.
     */
    @Autowired
    private GroupService groupService;

    /**
     * Repository for accessing user data from the database.
     * Injected via Spring's dependency injection mechanism.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Service class to handle enrollment-related operations.
     * Injected via Spring's dependency injection mechanism.
     */
    @Autowired
    private EnrollmentsService enrollmentsService;

    /**
     * Creates a new group with the specified details.
     *
     * This endpoint allows authenticated users to create a new group by providing
     * the group name and list of employees to be added to the group.
     *
     * @param groupInDTO the Data Transfer Object containing group creation details
     *                   including group name and employees list
     * @return ResponseEntity containing a StandardResponseOutDTO with success message
     *         and HTTP status CREATED (201)
     * @throws UnauthorizedAccessException if authentication fails or user is not authorized
     */
    @PostMapping("/create-group")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> createGroup(@Valid @RequestBody final GroupInDTO groupInDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String username = principal.getUserEmail();
        log.info("Attempting to create a group with name: {}", groupInDTO.getGroupName());
        StandardResponseOutDTO<MessageOutDTO> response = groupService
                .createGroup(groupInDTO.getGroupName(), username, groupInDTO.getEmployees());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a group by its unique identifier.
     *
     * This endpoint permanently removes a group from the system.
     * All associated data and relationships will be cleaned up.
     *
     * @param groupId the unique identifier of the group to be deleted
     * @return ResponseEntity containing a StandardResponseOutDTO with success message
     *         and HTTP status OK (200)
     */
    @DeleteMapping("/remove/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> deleteGroup(@PathVariable final long groupId) {
        log.info("Attempting to delete group with ID: {}", groupId);
        StandardResponseOutDTO<MessageOutDTO> response = groupService.deleteGroup(groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing group's information.
     *
     * This endpoint allows modification of group details such as group name.
     * Only the group owner or authorized users can update group information.
     *
     * @param groupInDTO the Data Transfer Object containing updated group information
     *                   including group ID and new group name
     * @return ResponseEntity containing a StandardResponseOutDTO with success message
     *         and HTTP status OK (200)
     */
    @PutMapping("/update-group")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> updateGroup(@RequestBody final GroupInDTO groupInDTO) {
        log.info("Updating Group details");
        StandardResponseOutDTO<MessageOutDTO> response = groupService
                .updateGroup(groupInDTO.getGroupId(), groupInDTO.getGroupName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Adds a user to an existing group.
     *
     * This endpoint allows authorized users to add new members to a group.
     * The requesting user must have appropriate permissions to modify group membership.
     *
     * @param groupInDTO the Data Transfer Object containing user and group identifiers
     *                   for the membership addition operation
     * @return ResponseEntity containing a StandardResponseOutDTO with success message
     *         and HTTP status OK (200)
     * @throws UnauthorizedAccessException if authentication fails or user lacks permissions
     */
    @PostMapping("/add-user")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> addUserToGroup(@Valid @RequestBody final GroupInDTO groupInDTO) {
        log.info("Attempting to add user with ID: to group with ID: {}", groupInDTO.getGroupId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }

        String username = principal.getUserEmail();
        StandardResponseOutDTO<MessageOutDTO> response = groupService.addUserToGroup(groupInDTO, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Removes a user from a group.
     *
     * This endpoint allows authorized users to remove members from a group.
     * The user will lose access to group-specific resources and permissions.
     *
     * @param groupdto the Data Transfer Object containing user and group identifiers
     *                 for the membership removal operation
     * @return ResponseEntity containing a StandardResponseOutDTO with success message
     *         and HTTP status OK (200)
     */
    @DeleteMapping("/remove-user")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> removeUserFromGroup(
            @Valid @RequestBody final GroupInDTO groupdto) {
        log.info("Attempting to remove user with ID: {} from group with ID: {}", groupdto.getUserId(), groupdto.getGroupId());
        StandardResponseOutDTO<MessageOutDTO> response = groupService
                .removeUserFromGroup(groupdto.getUserId(), groupdto.getGroupId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves the list of users in a specific group.
     *
     * This endpoint returns detailed information about all users who are members
     * of the specified group, including their roles and permissions within the group.
     *
     * @param groupId the unique identifier of the group whose members are to be retrieved
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupUserOutDTO
     *         and HTTP status OK (200) if users found, or NO_CONTENT (204) if no users found
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

    /**
     * Retrieves course details associated with a specific group.
     *
     * This endpoint returns information about all courses that are assigned to
     * or accessible by members of the specified group.
     *
     * @param groupId the unique identifier of the group whose course details are to be retrieved
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupCourseOutDTO
     *         and HTTP status OK (200)
     */
    @GetMapping("/group-courses/{groupId}")
    public ResponseEntity<StandardResponseOutDTO<List<GroupCourseOutDTO>>> getCourseDetails(@PathVariable final long groupId) {
        log.info("Attempting to get course details of groupId : {}", groupId);
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
     * Retrieves all groups associated with the currently authenticated user.
     *
     * This endpoint returns a list of groups that the authenticated user has access to,
     * either as a member or as an administrator.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupOutDTO
     *         and HTTP status OK (200) if groups found, or NO_CONTENT (204) if no groups found
     * @throws UnauthorizedAccessException if authentication fails
     */
    @GetMapping("/groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getGroups() {
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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all groups in the system.
     *
     * This endpoint returns a comprehensive list of all groups, regardless of
     * the current user's membership status. Typically used for administrative purposes.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupOutDTO
     *         and HTTP status OK (200) if groups found, or NO_CONTENT (204) if no groups found
     */
    @GetMapping("/Allgroups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getAllGroups() {
        log.info("Fetching groups ");
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getAllGroups();

        if (response.getData().isEmpty()) {
            log.warn("No groups found ");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups ", response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all active groups in the system.
     *
     * This endpoint returns a list of groups that are currently active and operational.
     * Inactive or archived groups are excluded from the results.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupOutDTO
     *         and HTTP status OK (200) if active groups found, or NO_CONTENT (204) if no active groups found
     */
    @GetMapping("/all-active-groups")
    public ResponseEntity<StandardResponseOutDTO<List<GroupOutDTO>>> getAllActiveGroups() {
        log.info("Fetching groups ");
        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getAllActiveGroups();

        if (response.getData().isEmpty()) {
            log.warn("No groups found ");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        log.info("Found {} groups ", response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves the total count of groups in the system.
     *
     * This endpoint returns a numerical count of all groups, which can be useful
     * for dashboard displays, pagination calculations, or administrative reporting.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with Long value representing
     *         the total group count and HTTP status OK (200)
     */
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

    /**
     * Retrieves a summary of recently created or modified groups.
     *
     * This endpoint returns a list of groups that have been recently created or updated,
     * typically used for dashboard displays or activity feeds.
     *
     * @return ResponseEntity containing a StandardResponseOutDTO with list of GroupSummaryOutDTO
     *         and HTTP status OK (200)
     */
    @GetMapping("/recent")
    public ResponseEntity<StandardResponseOutDTO<List<GroupSummaryOutDTO>>> getRecentGroups() {
        StandardResponseOutDTO<List<GroupSummaryOutDTO>> response = groupService.getRecentGroupSummaries();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves courses associated with a specific user within a group context.
     *
     * This endpoint returns detailed information about courses that are accessible to
     * a specific user within the context of a particular group.
     *
     * @param groupInDTO the Data Transfer Object containing group ID and user ID
     *                   for the course retrieval operation
     * @return ResponseEntity containing a StandardResponseOutDTO with list of CourseInfoOutDTO
     *         and HTTP status OK (200)
     */
    @PostMapping("/user-courses")
    public ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> getUserCoursesInGroups(@RequestBody GroupInDTO groupInDTO){
        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = groupService.getUserCourses(groupInDTO.getGroupId(), groupInDTO.getUserId());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Retrieves bundles associated with a specific user within a group context.
     *
     * This endpoint returns detailed information about courses that are accessible to
     * a specific user within the context of a particular group.
     *
     * @param groupInDTO the Data Transfer Object containing group ID and user ID
     *                   for the course retrieval operation
     * @return ResponseEntity containing a StandardResponseOutDTO with list of BundleInfoOutDTO
     *         and HTTP status OK (200)
     */
    @PostMapping("/user-bundles")
    public ResponseEntity<StandardResponseOutDTO<List<BundleOutDTO>>> getUserBundlesInGroups(@RequestBody GroupInDTO groupInDTO){
        StandardResponseOutDTO<List<BundleOutDTO>> response = groupService.getUserBundles(groupInDTO.getGroupId(), groupInDTO.getUserId());
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


    @GetMapping("/bundles")
    public ResponseEntity<StandardResponseOutDTO<List<GroupBundleOutDTO>>> getGroupBundles(@RequestParam Long groupId){
        StandardResponseOutDTO<List<GroupBundleOutDTO>> response = groupService.getGroupBundles(groupId);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

}
