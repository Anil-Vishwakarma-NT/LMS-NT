package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.BundleInfoDTO;
import com.nt.LMS.dto.EnrolledUserDTO;
import com.nt.LMS.dto.UserBundleEnrollmentOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserBundleEnrollment;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.UserBundleEnrollmentRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.UserBundleEnrollmentService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBundleEnrollmentServiceImpl implements UserBundleEnrollmentService {

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBundleEnrollmentRepository userBundleEnrollmentRepository;

    @Override
    public List<UserBundleEnrollmentOutDTO> getUserEnrollmentsByBundle() {
        try {
            List<BundleInfoDTO> bundleInfoDTOS = courseMicroserviceClient.getBundleInfo().getBody();
            if(bundleInfoDTOS == null || bundleInfoDTOS.isEmpty()) {
                throw new ResourceNotFoundException("No course added to the bundle");
            }
            List<UserBundleEnrollmentOutDTO> userBundleEnrollmentOutDTOS = new ArrayList<>();
            for(BundleInfoDTO bundleInfoDTO : bundleInfoDTOS) {
                long individualEnrollments = userBundleEnrollmentRepository.countByBundleIdAndStatusNotIn(bundleInfoDTO.getBundleId(),
                        List.of("EXPIRED", "UNENROLLED", "COMPLETED"));
                UserBundleEnrollmentOutDTO userBundleEnrollmentOutDTO = new UserBundleEnrollmentOutDTO();
                userBundleEnrollmentOutDTO.setBundleName(bundleInfoDTO.getBundleName());
                userBundleEnrollmentOutDTO.setTotalCourses(bundleInfoDTO.getTotalCourses());
                userBundleEnrollmentOutDTO.setActive(bundleInfoDTO.isActive());
                userBundleEnrollmentOutDTO.setAverageCompletion(98F);

                List<UserBundleEnrollment> userBundleEnrollments = userBundleEnrollmentRepository.findByBundleId(bundleInfoDTO.getBundleId());
                if (userBundleEnrollments != null && !userBundleEnrollments.isEmpty()) {
                    //throw new ResourceNotFoundException("No bundle enrollments for user");
                    List<EnrolledUserDTO> enrolledUserDTOS = new ArrayList<>();
                    for (UserBundleEnrollment userBundleEnrollment : userBundleEnrollments) {
                        User user = userRepository.findById(userBundleEnrollment.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                        User assignedByName = userRepository.findById(userBundleEnrollment.getAssignedBy())
                                .orElseThrow(() -> new ResourceNotFoundException("Assigner not found"));
                        EnrolledUserDTO enrolledDTO = getEnrolledUserDTO(userBundleEnrollment, user, assignedByName);
                        enrolledUserDTOS.add(enrolledDTO);
                    }
                    userBundleEnrollmentOutDTO.setEnrolledUserDTOList(enrolledUserDTOS);
                    userBundleEnrollmentOutDTO.setIndividualEnrollments(individualEnrollments);
                    userBundleEnrollmentOutDTOS.add(userBundleEnrollmentOutDTO);
                }
            }
            return userBundleEnrollmentOutDTOS;
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (FeignException e) {
          throw new RuntimeException("Error connecting with course service");
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }

    private static EnrolledUserDTO getEnrolledUserDTO(UserBundleEnrollment userBundleEnrollment, User user, User assignedByName) {
        EnrolledUserDTO enrolledDTO = new EnrolledUserDTO();
        enrolledDTO.setUserId(user.getUserId());
        enrolledDTO.setUserName(user.getFirstName() + " " + user.getLastName());
        enrolledDTO.setEnrollmentDate(userBundleEnrollment.getAssignedAt());
        enrolledDTO.setDeadline(userBundleEnrollment.getDeadline());
        enrolledDTO.setProgress(98F);
        enrolledDTO.setAssignedByName(assignedByName.getFirstName() + assignedByName.getLastName());
        return enrolledDTO;
    }
}
