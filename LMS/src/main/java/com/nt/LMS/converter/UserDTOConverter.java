package com.nt.LMS.converter;

import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.nt.LMS.constants.UserConstants.USER_NOT_FOUND;
@Component
public class UserDTOConverter {
    public UserOutDTO userToOutDto(User user , String manager){
        UserOutDTO userout = new UserOutDTO();
        userout.setUserId(user.getUserId());
        userout.setUsername(user.getUserName());
        userout.setEmail(user.getEmail());
        userout.setFirstName(user.getFirstName());
        userout.setLastName(user.getLastName());
        if (manager != null) {
            userout.setManager(manager);
        }
        return userout;
    }
}
