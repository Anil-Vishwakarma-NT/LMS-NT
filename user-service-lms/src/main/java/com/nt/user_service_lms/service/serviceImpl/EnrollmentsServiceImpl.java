//package com.nt.user_service_lms.service.serviceImpl;
//
//import com.nt.user_service_lms.dto.outDTO.GroupCourseOutDTO;
//import com.nt.user_service_lms.dto.outDTO.GroupUserOutDTO;
//import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
//import com.nt.user_service_lms.entities.Enrollment;
//import com.nt.user_service_lms.entities.User;
//import com.nt.user_service_lms.entities.UserGroup;
//import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
//import com.nt.user_service_lms.repository.EnrollmentRepository;
//import com.nt.user_service_lms.repository.UserGroupRepository;
//import com.nt.user_service_lms.repository.UserRepository;
//import com.nt.user_service_lms.service.EnrollmentsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//
//@Service
//public class EnrollmentsServiceImpl implements EnrollmentsService {
//
//    @Autowired
//    EnrollmentRepository enrollmentRepository;
//
//    @Autowired
//    private CourseMicroserviceClient courseMicroserviceClient;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserGroupRepository userGroupRepository;
//
//    @Override
//    public StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(long groupId) {
//
//        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
//
//
//        Map<Long, GroupCourseOutDTO> mp = new HashMap<>();
//
//        for (Enrollment en : enrols) {
//            String courseName = courseMicroserviceClient.getCourseNameById(en.getCourseId()).getBody();
//            GroupCourseOutDTO gc = mp.getOrDefault(en.getCourseId(), new GroupCourseOutDTO());
//            long totalenrols = gc.getEnrols() + 1;
////            double userprogress = courseMicroserviceClient.getCourseProgress( en.getUserId().intValue() ,en.getCourseId());
//            double progress = ((gc.getProgress() * gc.getEnrols()) + 0) / totalenrols;
//            gc.setCourseName(courseName);
//            gc.setCourseId(en.getCourseId());
//            gc.setEnrols(totalenrols);
//            gc.setProgress(progress);
//
//            mp.put(en.getCourseId(), gc);
//        }
//
//
//        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
//    }
//
//    @Override
//    public StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(long groupId) {
//
//        List<Enrollment> enrols = enrollmentRepository.findByGroupId(groupId);
//        List<UserGroup> usrgrp = userGroupRepository.findAllByGroupId(groupId);
//
//        Map<Long, GroupUserOutDTO> mp = new HashMap<>();
//
//        for (Enrollment en : enrols) {
//            Optional<User> usr = userRepository.findById(en.getUserId());
//            GroupUserOutDTO uc = mp.getOrDefault(en.getUserId(), new GroupUserOutDTO());
//            long totalenrols = uc.getEnrols() + 1;
////            double userprogress = courseMicroserviceClient.getCourseProgress( en.getUserId().intValue() ,en.getCourseId());
//            double progress = ((uc.getProgress() * uc.getEnrols()) + 0) / totalenrols;
//            uc.setFirstName(usr.get().getFirstName());
//            uc.setLastName(usr.get().getLastName());
//            uc.setUserId(en.getUserId());
//            uc.setEnrols(totalenrols);
//            uc.setProgress(progress);
//
//            mp.put(en.getUserId(), uc);
//        }
//
//        for (UserGroup user : usrgrp) {
//
//            if (!mp.containsKey(user.getUserId()) && user.isActive()) {
//                Optional<User> usr = userRepository.findById(user.getUserId());
//                GroupUserOutDTO uc = new GroupUserOutDTO();
//                long totalenrols = 0;
//                double progress = 0;
//                uc.setFirstName(usr.get().getFirstName());
//                uc.setLastName(usr.get().getLastName());
//                uc.setUserId(usr.get().getUserId());
//                uc.setEnrols(totalenrols);
//                uc.setProgress(progress);
//
//                mp.put(user.getUserId(), uc);
//
//            }
//        }
//
//
//        return StandardResponseOutDTO.success(new ArrayList<>(mp.values()), "Successfully fetched course details.");
//    }
//}
