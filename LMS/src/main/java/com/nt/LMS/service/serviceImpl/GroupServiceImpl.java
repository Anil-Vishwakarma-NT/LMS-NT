package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.converter.GroupDTOConverter;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.inDTO.GroupInDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserGroup;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.UnauthorizedAccessException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.EnrollmentRepository;
import com.nt.LMS.repository.GroupRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.EnrollmentsService;
import com.nt.LMS.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

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
    public StandardResponseOutDTO<MessageOutDto> createGroup(final String groupName, final String username , final List<Long> employeeId) {
        try {
            log.info("Attempting to create a group with name: {} by user: {}", groupName, username);
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));

            Group group = new Group(groupName, user.getUserId());
            group = groupRepository.save(group);
                    log.info("Group '{}' created successfully by '{}'", groupName, username);
           if(!employeeId.isEmpty()){
               Group finalGroup = group;
               employeeId.forEach(employee -> {
                   UserGroup userGroup= new UserGroup(employee,finalGroup.getGroupId());
                   userGroupRepository.save(userGroup);
               });
           }
            MessageOutDto messageOutDto = new MessageOutDto(GROUP_CREATED);
            return StandardResponseOutDTO.success(messageOutDto,GROUP_CREATED);
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
    @Transactional
    public StandardResponseOutDTO<MessageOutDto> deleteGroup(final long groupId) {
        try {
            log.info("Attempting to delete group with ID: {}", groupId);
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND));
            enrollmentRepository.softDeleteByGroupId(groupId);
            userGroupRepository.softDeleteByGroupId(groupId);
            groupRepository.softDeleteByGroupId(groupId);
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
     * @param employees the user ID
     * @param groupId the group ID
     * @return a success or failure message
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> addUserToGroup(final GroupInDTO groupInDTO ,String username) {
        try {
            log.info("Adding user ID: to group ID: {}", groupInDTO.getGroupId());

            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UnauthorizedAccessException(USER_NOT_FOUND));

            if (groupRepository.findById(groupInDTO.getGroupId()).isEmpty()) {
                throw new ResourceNotFoundException(GROUP_NOT_FOUND);
            }

            for(Long id :groupInDTO.getEmployees()) {
                if (userRepository.findById(id).isEmpty()) {
                    throw new ResourceNotFoundException(USER_NOT_FOUND);
                }
                Optional<UserGroup> usergroup = userGroupRepository.findByUserIdAndGroupId(id,groupInDTO.getGroupId());
                if(usergroup.isPresent()){
                    usergroup.get().set_active(true);
                    userGroupRepository.save(usergroup.get());
                }
                else {
                    UserGroup userGroup = new UserGroup(id, groupInDTO.getGroupId());
                    userGroupRepository.save(userGroup);
                }
            }

            if(!groupInDTO.getCourses().isEmpty()){
                for(Long courseId : groupInDTO.getCourses()) {
                    for(Long userId:groupInDTO.getEmployees()) {
                        Optional<Enrollment> existing = enrollmentRepository.findByGroupIdAndUserIdAndCourseId(groupInDTO.getGroupId(), userId, courseId);
                        if (existing.isPresent()) {
                            existing.get().setActive(true);
                            enrollmentRepository.save(existing.get());
                        } else {
                            Enrollment enrol = new Enrollment();
                            enrol.setGroupId(groupInDTO.getGroupId());
                            enrol.setUserId(userId);
                            enrol.setCourseId(courseId);
                            enrol.setAssignedBy(user.getUserId());
                            enrol.setAssignedAt(groupInDTO.getAssignedAt());
                            enrol.setDeadline(groupInDTO.getDeadline());
                            enrol.setStatus("active");
                            enrol.setEnrollmentSource("GROUP");
                            enrol.setCreatedAt(groupInDTO.getAssignedAt());
                            enrol.setUpdatedAt(groupInDTO.getAssignedAt());
                            enrollmentRepository.save(enrol);
                        }
                    }
                }
            }

            MessageOutDto messageOutDto =  new MessageOutDto(USER_ADDED_TO_GROUP);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error adding user ID: to group ID: {}", groupInDTO.getGroupId(), e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    @Override
    public StandardResponseOutDTO<MessageOutDto> updateGroup(long groupId, String groupName) {
         try{
             Optional<Group> group = groupRepository.findById(groupId);
             if(group.isPresent()){
                 group.get().setGroupName(groupName);
                 groupRepository.save(group.get());
             }
             else {
                throw new ResourceNotFoundException("Group Not found");
             }
             MessageOutDto messageOutDto =  new MessageOutDto("Group updated");
             return StandardResponseOutDTO.success(messageOutDto,null);
         }
         catch (Exception e){
             log.error("Error in updating group");
             throw new RuntimeException("Group not updated",e);
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

            userGroupRepository.softDeleteByGroupIdAndUserId(userGroup.getGroupId(),userGroup.getUserId());
            enrollmentRepository.softDeleteByGroupIdAndUserId(userGroup.getGroupId(),userGroup.getUserId());
            MessageOutDto messageOutDto = new MessageOutDto(USER_REMOVED_SUCCESSFULLY);
            return StandardResponseOutDTO.success(messageOutDto,null);
        } catch (Exception e) {
            log.error("Error removing user ID: {} from group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
        }
    }

    public StandardResponseOutDTO<List<CourseInfoOutDTO>> getUserCourses(final long groupId, final long userId ){
        try{
            List<Enrollment> enrols = enrollmentRepository.findByGroupIdAndUserId(groupId,userId);
            System.out.println("Enrols has values  " + enrols.isEmpty() + " " + groupId + "  " + userId);
            log.info("enrols fetched");
            List<CourseInfoOutDTO> courses = courseMicroserviceClient.getCourseInfo().getBody().getData();
            List<Enrollment> groupcourses = enrollmentRepository.findByGroupId(groupId);
            log.info("courses fetched");
            Set<Long> enrolledCourseIds = enrols.stream()
                    .map(Enrollment::getCourseId)
                    .collect(Collectors.toSet());
            Set<Long> enrolledGroupCourseIds = groupcourses.stream()
                    .map(Enrollment::getCourseId)
                    .collect(Collectors.toSet());
            log.info("enrolled courses  fetched");
            List<CourseInfoOutDTO> notEnrolledCourses = courses.stream()
                    .filter(course -> !enrolledCourseIds.contains(course.getCourseId()) && enrolledGroupCourseIds.contains(course.getCourseId()))
                    .collect(Collectors.toList());

            return  StandardResponseOutDTO.success(notEnrolledCourses , null);

        }catch (Exception e) {
            log.error("Error collecting courses for  user ID: {} from group ID: {}", userId, groupId, e);
            throw new RuntimeException(GROUP_FAILURE, e);
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
            if (user.getUserId() == UserConstants.getAdminId()) {
                List<Group> adminGroups = groupRepository.findAll();
                for (Group group : adminGroups) {
                    if (group.is_active()) {
                        GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                                user.getFirstName() + " " + user.getLastName());
                        groupOutList.add(gout);
                    }
                }
            }
            else {
                List<Group> userGroups = groupRepository.findByCreatorId(user.getUserId());

                for (Group group : userGroups) {
                    if (group.is_active()) {
                        GroupOutDTO gout = groupDTOConverter.groupToOutDto(group,
                                user.getFirstName() + " " + user.getLastName());
                        groupOutList.add(gout);
                    }
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
    public StandardResponseOutDTO<List<GroupSummaryOutDTO>> getRecentGroupSummaries() {
        // Get the 5 most recent groups
        List<Group> recentGroups = groupRepository.findTop5ByOrderByGroupIdDesc();
        List<GroupSummaryOutDTO> groupSummary =  convertToGroupSummaries(recentGroups);
        return StandardResponseOutDTO.success(groupSummary, "Group summary fetched successfully");
    }

    /**
     * Converts a list of Group entities to GroupSummaryDTOs with member counts using streams.
     *
     * @param groups the list of groups to convert
     * @return a list of GroupSummaryDTOs
     */
    private List<GroupSummaryOutDTO> convertToGroupSummaries(List<Group> groups) {
        return groups.stream()
                .map(group -> {
                    // Get member count
                    long memberCount = userGroupRepository.findAllByGroupId(group.getGroupId()).size();

                    // Get creator name - assuming you have a method to get user by ID
                    String creatorName = userRepository.findById(group.getCreatorId())
                            .map(user -> user.getFirstName() + " " + user.getLastName())
                            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

                    return new GroupSummaryOutDTO(
                            group.getGroupId(),
                            group.getGroupName(),
                            creatorName,
                            memberCount
                    );
                })
                .collect(Collectors.toList());
    }



    @Override
    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(long groupId) {

        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);


        Map<Long , GroupCourseOutDTO > mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive()) {
                String courseName = courseMicroserviceClient.getCourseNameById(en.getCourseId()).getBody();
                GroupCourseOutDTO gc = mp.getOrDefault(en.getCourseId(), new GroupCourseOutDTO());
                long totalenrols = gc.getEnrols() + 1;
                double userprogress = courseMicroserviceClient.getCourseProgressWithMeta(en.getUserId().intValue(), en.getCourseId().intValue()).getCourseCompletionPercentage();
                double progress = ((gc.getProgress() * gc.getEnrols()) + userprogress) / totalenrols;
                gc.setCourseName(courseName);
                gc.setCourseId(en.getCourseId());
                gc.setEnrols(totalenrols);
                gc.setProgress(progress);
                mp.put(en.getCourseId(), gc);
            }
        }



        return StandardResponseOutDTO.success( new ArrayList<>(mp.values()),"Successfully fetched course details.");
    }

    @Override
    public StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(long groupId) {

        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
        List<UserGroup> usrgrp = userGroupRepository.findAllByGroupId(groupId);

        Map<Long , GroupUserOutDTO > mp = new HashMap<>();

        for (Enrollment en : enrols) {
            if (en.getIsActive()) {
                Optional<User> usr = userRepository.findById(en.getUserId());
                GroupUserOutDTO uc = mp.getOrDefault(en.getUserId(), new GroupUserOutDTO());
                long totalenrols = uc.getEnrols() + 1;
                double userprogress = courseMicroserviceClient.getCourseProgressWithMeta(en.getUserId().intValue(), en.getCourseId().intValue()).getCourseCompletionPercentage();
                double progress = ((uc.getProgress() * uc.getEnrols()) + userprogress) / totalenrols;
                uc.setFirstName(usr.get().getFirstName());
                uc.setLastName(usr.get().getLastName());
                uc.setUserId(en.getUserId());
                uc.setEnrols(totalenrols);
                uc.setProgress(progress);
                mp.put(en.getUserId(), uc);
            }
        }

        for(UserGroup user : usrgrp){
            if(!mp.containsKey(user.getUserId()) && user.is_active()){
                Optional<User> usr = userRepository.findById(user.getUserId());
                GroupUserOutDTO uc = new GroupUserOutDTO();
                long totalenrols = 0;
                double progress = 0;
                uc.setFirstName(usr.get().getFirstName());
                uc.setLastName(usr.get().getLastName());
                uc.setUserId(usr.get().getUserId());
                uc.setEnrols(totalenrols);
                uc.setProgress(progress);

                mp.put(user.getUserId(),uc);

            }
        }
        return StandardResponseOutDTO.success( new ArrayList<>(mp.values()),"Successfully fetched course details.");
    }
}
