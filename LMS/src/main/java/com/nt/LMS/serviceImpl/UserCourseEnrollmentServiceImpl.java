package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.EnrolledUserDTO;
import com.nt.LMS.dto.CourseInfoDTO;
import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;
import com.nt.LMS.dto.UserEnrollDetails;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserCourseEnrollment;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.UserCourseEnrollmentRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.UserCourseEnrollmentService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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
                User owner = userRepository.findById(courseInfo.getOwnerId())
                        .filter(User::isActive)
                        .orElseThrow(() -> new ResourceNotFoundException("Active owner not found"));

                List<UserCourseEnrollment> indivisualEnrollments = userCourseEnrollmentRepository
                        .findByCourseId(courseInfo.getCourseId());

                long activeEnrollmentCount = indivisualEnrollments.stream()
                        .filter(enrollment -> !List.of("EXPIRED", "UNENROLLED", "COMPLETED").contains(enrollment.getStatus()))
                        .map(enrollment -> userRepository.findById(enrollment.getUserId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(user -> user.isActive() && user.getUserId() != 1) // optional: exclude user ID 1
                        .count();

                UserCourseEnrollmentOutDTO courseDTO = new UserCourseEnrollmentOutDTO();
                courseDTO.setCourseName(courseInfo.getTitle());
                courseDTO.setOwnerId(owner.getUserId());
                courseDTO.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
                courseDTO.setActive(courseInfo.isActive());
                courseDTO.setIndividualEnrollments(activeEnrollmentCount);

                List<UserCourseEnrollment> enrollments = userCourseEnrollmentRepository.findByCourseId(courseInfo.getCourseId());
                List<EnrolledUserDTO> enrolledUsersDTO = new ArrayList<>();

                for (UserCourseEnrollment enrollment : enrollments) {
                    Optional<User> userOpt = userRepository.findById(enrollment.getUserId())
                            .filter(user -> user.isActive() && user.getUserId() != 1);
                    Optional<User> assignedByOpt = userRepository.findById(enrollment.getAssignedBy())
                            .filter(User::isActive);

                    if (userOpt.isEmpty() || assignedByOpt.isEmpty()) {
                        continue; // Skip inactive users or assigners
                    }

                    User user = userOpt.get();
                    User assignedByName = assignedByOpt.get();

                    Double progress = 0.0;
                    try {
                        ResponseEntity<Double> progressResponse = courseMicroserviceClient.getCourseProgress(
                                enrollment.getUserId().intValue(), enrollment.getCourseId().intValue());
                        if (progressResponse.getBody() != null) {
                            progress = progressResponse.getBody();
                        }
                    } catch (Exception ex) {
                        // Log the exception if necessary, but continue with default progress
                        progress = 0.0;
                    }

                    EnrolledUserDTO enrolledDTO = getEnrolledUserDTO(enrollment, user, assignedByName, progress);
                    enrolledUsersDTO.add(enrolledDTO);
                }

                courseDTO.setEnrolledUserDTOList(enrolledUsersDTO);
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

    private static EnrolledUserDTO getEnrolledUserDTO(UserCourseEnrollment enrollment, User user, User assignedByName,double progress) {
        EnrolledUserDTO enrolledDTO = new EnrolledUserDTO();
        enrolledDTO.setUserId(user.getUserId());
        enrolledDTO.setUserName(user.getFirstName() + " " + user.getLastName());
        enrolledDTO.setEnrollmentDate(enrollment.getAssignedAt());
        enrolledDTO.setDeadline(enrollment.getDeadline());
        enrolledDTO.setProgress(progress);
        enrolledDTO.setAssignedByName(assignedByName.getFirstName() + assignedByName.getLastName());
        return enrolledDTO;
    }


    @Override
    public List<UserEnrollDetails> getUserEnrolledCourses(Long userId) {
        List<UserCourseEnrollment> enrollments = userCourseEnrollmentRepository.findByUserId(userId);
        if (enrollments.isEmpty()) {
            log.warn("No enrolled courses found for user ID: " + userId);
            throw new ResourceNotFoundException("No enrolled courses found for user ID: " + userId);
        }

        List<UserEnrollDetails> responseList = new ArrayList<>();
        for (UserCourseEnrollment enrollment : enrollments) {
            UserEnrollDetails dto = new UserEnrollDetails();
            dto.setCourseId(enrollment.getCourseId());
            dto.setAssignedById(enrollment.getAssignedBy());
            dto.setEnrollmentDate(enrollment.getAssignedAt());
            dto.setDeadline(enrollment.getDeadline());
            responseList.add(dto);
        }

        return responseList;
    }


}
