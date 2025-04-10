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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.nt.LMS.constants.UserConstants.USER_NOT_FOUND;

@Service
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
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Group group = new Group(group_name, user.getUserId());
            groupRepository.save(group);} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String delGroup(long group_id){
        try {
            groupRepository.deleteById(group_id);
            return "Group created successfully";
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public String addUserToGroup(long user_id , long group_id){
        try {
        Group group = groupRepository.findById(group_id);
        Set<User> users = group.getUsers();
        Optional<User> userOpt = userRepository.findById(user_id);
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User with ID " +  " not found"));
        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);
        return "Group created";
        } catch (ResourceNotFoundException e) {
           throw new ResourceNotFoundException(USER_NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);        }
    }

    public String  removeUserFromGroup(long userId , long group_id){
        try {
        Group group = groupRepository.findById(group_id);
        Optional<User> userOpt = userRepository.findById(userId);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User with ID " + " not found"));
        Set<User> users = group.getUsers();
        if (users.contains(user)) {
            users.remove(user);  // Remove the user from the group
            group.setUsers(users);
            groupRepository.save(group);
            return "User removed";// Save the updated group
        } else {
            throw new ResourceNotFoundException("User is not part of the group.");
        }
        } catch (Exception e) {
           throw new RuntimeException(e);
                  }
    }


    public List<UserOutDTO> getUsersInGroup(long group_id){
        try {
        Group group = groupRepository.findById(group_id);
        Set<User> users = group.getUsers();
        List<UserOutDTO> response = new ArrayList<>();
            for(User u : users){
                String managername = "";
                UserOutDTO userdto = userDTOConverter.userToOutDto(u , managername);
                response.add(userdto);
            }
        return response;
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public List<GroupOutDTO> getGroups(long userId){
        try{
        List<Group> groups = groupRepository.getGroups(userId);
        Optional<User> creatorOp = userRepository.findById(userId);
        User creator = creatorOp.orElseThrow( () -> new ResourceNotFoundException(USER_NOT_FOUND));
        List<GroupOutDTO> groupout = new ArrayList<>();
        for(Group group : groups){

            GroupOutDTO gout = groupDTOConverter.groupToOutDto(group ,creator.getFirstName()+creator.getLastName());
            groupout.add(gout);

        }
        return groupout;
        }
        catch(Exception e){
            throw new RuntimeException(e);        }
    }

    public List<GroupOutDTO> getAllGroups(){
        try{
        List<Group> groups = groupRepository.findAll();
            List<GroupOutDTO> groupout = new ArrayList<>();
            for(Group group : groups){
                Optional<User> creatorOp = userRepository.findById(group.getCreatorId());
                User creator = creatorOp.orElseThrow( () -> new ResourceNotFoundException(USER_NOT_FOUND));
                GroupOutDTO gout = groupDTOConverter.groupToOutDto(group ,creator.getFirstName()+creator.getLastName());
                groupout.add(gout);

            }
        return groupout;
        }
        catch(Exception e){
            throw new RuntimeException(e);
                    }
    }




}
