package com.nt.LMS.service;

import com.nt.LMS.dto.outDTO.GroupCourseOutDTO;
import com.nt.LMS.dto.outDTO.GroupUserOutDTO;
import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.entities.Enrollment;

import java.util.List;

public interface EnrollmentsService {


    StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(long groupId);
    public StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(long groupId);

}
