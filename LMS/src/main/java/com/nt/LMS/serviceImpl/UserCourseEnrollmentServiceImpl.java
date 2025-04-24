package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.CourseEnrolledUserDTO;
import com.nt.LMS.dto.CourseInfoDTO;
import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserCourseEnrollment;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.UserCourseEnrollmentRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.UserCourseEnrollmentService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserCourseEnrollmentServiceImpl implements UserCourseEnrollmentService {

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseEnrollmentRepository userCourseEnrollmentRepository;

    @Override
    public List<UserCourseEnrollmentOutDTO> getUserEnrollmentsByCourse() {
        try {
            List<CourseInfoDTO> courseInfoDTOLists = courseMicroserviceClient.getCourseInfo().getBody();
            if (courseInfoDTOLists == null || courseInfoDTOLists.isEmpty()) {
                throw new ResourceNotFoundException("Course not found");
            }

            List<UserCourseEnrollmentOutDTO> responseList = new ArrayList<>();

            for (CourseInfoDTO courseInfo : courseInfoDTOLists) {

                // Fetch course owner
                User owner = userRepository.findById(courseInfo.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

                long activeEnrollmentCount = userCourseEnrollmentRepository
                        .countByCourseIdAndStatusNotIn(
                                courseInfo.getCourseId(),
                                List.of("EXPIRED", "UNENROLLED", "COMPLETED")
                        );

                UserCourseEnrollmentOutDTO courseDTO = new UserCourseEnrollmentOutDTO();
                courseDTO.setCourseName(courseInfo.getTitle());
                courseDTO.setOwnerId(owner.getUserId());
                courseDTO.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
                courseDTO.setActive(courseInfo.isActive());
                courseDTO.setIndividualEnrollments(activeEnrollmentCount);

                List<UserCourseEnrollment> enrollments = userCourseEnrollmentRepository.findByCourseId(courseInfo.getCourseId());
                List<CourseEnrolledUserDTO> enrolledUsersDTO = new ArrayList<>();

                for (UserCourseEnrollment enrollment : enrollments) {
                    User user = userRepository.findById(enrollment.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    CourseEnrolledUserDTO enrolledDTO = new CourseEnrolledUserDTO();
                    enrolledDTO.setUserId(user.getUserId());
                    enrolledDTO.setUserName(user.getFirstName() + " " + user.getLastName());
                    enrolledDTO.setEnrollmentDate(enrollment.getAssignedAt());
                    enrolledDTO.setDeadline(enrollment.getDeadline());
                    enrolledDTO.setProgress(98L);
                    enrolledUsersDTO.add(enrolledDTO);
                }

                courseDTO.setCourseEnrolledUserDTOList(enrolledUsersDTO);
                responseList.add(courseDTO);
            }

            return responseList;

        } catch (FeignException e) {
            throw new RuntimeException("Unable to connect with course microservice", e);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }


}
