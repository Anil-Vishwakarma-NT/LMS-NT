package com.nt.LMS.service;



import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }


    // Admin apis -------------------------------------------------------->
    public void empDel(Long id){
        Optional<User> user = userRepository.findById(id);
        userRepository.deleteById(id);
    }

    public void managerDel(Long id){
       Optional<User> manager = userRepository.findById(id);
       if(manager == null){
           throw new IllegalArgumentException("manager not present");
       }

       userRepository.updateManager(1L , manager.get().getUserId());
       userRepository.deleteById(manager.get().getUserId());
       return ;

    }

    public void changeRole(User user , Role role){
        long count = userRepository.updateRole(role.getRole_id() , user.getUserId());
    }



    //------------------------------------------------------------------->


}
