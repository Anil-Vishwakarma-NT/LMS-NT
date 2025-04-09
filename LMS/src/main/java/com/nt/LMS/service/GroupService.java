package com.nt.LMS.service;

import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.GroupRepository;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nt.LMS.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public void addUserToGroup(long user_id , long group_id){
        Group group = groupRepository.findById(group_id);
        Set<User> users = group.getUsers();
        Optional<User> userOpt = userRepository.findById(user_id);
        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User with ID " +  " not found"));
        users.add(user);
        group.setUsers(users);
        groupRepository.save(group);
    }

    public Set<User> getUsersInGroup(long group_id){
        Group group = groupRepository.findById(group_id);
        Set<User> users = group.getUsers();
        return users;
    }

    public void removeUserInGroup(long userId , long group_id){
        Group group = groupRepository.findById(group_id);
        Optional<User> userOpt = userRepository.findById(userId);

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User with ID " + " not found"));
        Set<User> users = group.getUsers();
        if (users.contains(user)) {
            users.remove(user);  // Remove the user from the group
            group.setUsers(users);
            groupRepository.save(group);  // Save the updated group
        } else {
            throw new UserNotFoundException("User is not part of the group.");
        }
    }
    public void delGroup(long group_id){
        groupRepository.deleteById(group_id);
    }

    public void createGroup(String group_name , long user_id){
        Group group = new Group(group_name, user_id);
        groupRepository.save(group);
    }

    public List<Group> getGroups(long userId){
        List<Group> groups = groupRepository.getGroups(userId);
        return groups;
    }

    public List<Group> getAllGroups(){
        List<Group> groups = groupRepository.findAll();
        return groups;
    }




}
