package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.dto.outDTO.GroupCourseOutDTO;
import com.nt.LMS.dto.outDTO.GroupUserOutDTO;
import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserGroup;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.EnrollmentRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.EnrollmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EnrollmentsServiceImpl implements EnrollmentsService {

    @Autowired
    EnrollmentRepository enrollmentRepository;


    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Override
    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(long groupId) {

           List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);


        Map<Long , GroupCourseOutDTO > mp = new HashMap<>();

        for (Enrollment en : enrols){
            String courseName = courseMicroserviceClient.getCourseNameById(en.getCourseId()).getBody();
            GroupCourseOutDTO gc = mp.getOrDefault(en.getCourseId(),new GroupCourseOutDTO());
            long totalenrols = gc.getEnrols()+1;
//            double userprogress = courseMicroserviceClient.getCourseProgress( en.getUserId().intValue() ,en.getCourseId());
            double progress = ((gc.getProgress()*gc.getEnrols())+ 0)/totalenrols;
            gc.setCourseName(courseName);
            gc.setCourseId(en.getCourseId());
            gc.setEnrols(totalenrols);
            gc.setProgress(progress);

            mp.put(en.getCourseId(),gc);
        }



       return StandardResponseOutDTO.success( new ArrayList<>(mp.values()),"Successfully fetched course details.");
    }

   @Override
    public StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(long groupId) {

        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
        List<UserGroup> usrgrp = userGroupRepository.findAllByGroupId(groupId);

        Map<Long , GroupUserOutDTO > mp = new HashMap<>();

        for (Enrollment en : enrols){
            Optional<User> usr = userRepository.findById(en.getUserId());
            GroupUserOutDTO uc = mp.getOrDefault(en.getUserId(),new GroupUserOutDTO());
            long totalenrols = uc.getEnrols()+1;
//            double userprogress = courseMicroserviceClient.getCourseProgress( en.getUserId().intValue() ,en.getCourseId());
            double progress = ((uc.getProgress()*uc.getEnrols())+ 0)/totalenrols;
            uc.setFirstName(usr.get().getFirstName());
            uc.setLastName(usr.get().getLastName());
            uc.setUserId(en.getUserId());
            uc.setEnrols(totalenrols);
            uc.setProgress(progress);

            mp.put(en.getUserId(),uc);
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
//
//    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getUserAndCourse(long groupId , long userId) {
//
//
//
//    }
}
