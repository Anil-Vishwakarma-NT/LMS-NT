package com.nt.LMS.dto.outDTO;

import lombok.Data;

@Data
public class UserGroupOutDTO {

    String groupName;
    long groupId;
    double progress;
    long enrols;
}