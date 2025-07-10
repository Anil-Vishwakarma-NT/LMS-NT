package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

@Data
public class UserGroupOutDTO {

    String groupName;
    long groupId;
    double progress;
    long enrols;
}