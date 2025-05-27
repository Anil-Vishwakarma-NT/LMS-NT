package com.nt.LMS.service;

import com.nt.LMS.dto.UsersDetailsViewDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
    public long CountUsers();
    List<UsersDetailsViewDTO> getRecentUserDetails();

    public Map<String , Long> userStatistics(long userId);
}
