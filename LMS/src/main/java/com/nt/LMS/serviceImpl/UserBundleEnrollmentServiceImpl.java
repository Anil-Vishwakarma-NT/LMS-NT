package com.nt.LMS.serviceImpl;

import com.nt.LMS.outDTO.BundleInfoOutDTO;
import com.nt.LMS.outDTO.EnrolledUserOutDTO;
import com.nt.LMS.outDTO.UserBundleEnrollmentOutDTO;
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
            List<BundleInfoOutDTO> bundleInfoOutDTOS = courseMicroserviceClient.getBundleInfo().getBody();
            if(bundleInfoOutDTOS == null || bundleInfoOutDTOS.isEmpty()) {
                throw new ResourceNotFoundException("No course added to the bundle");
            }
            List<UserBundleEnrollmentOutDTO> userBundleEnrollmentOutDTOS = new ArrayList<>();
            for(BundleInfoOutDTO bundleInfoOutDTO : bundleInfoOutDTOS) {
                long individualEnrollments = userBundleEnrollmentRepository.countByBundleIdAndStatusNotIn(bundleInfoOutDTO.getBundleId(),
                        List.of("EXPIRED", "UNENROLLED", "COMPLETED"));
                UserBundleEnrollmentOutDTO userBundleEnrollmentOutDTO = new UserBundleEnrollmentOutDTO();
                userBundleEnrollmentOutDTO.setBundleName(bundleInfoOutDTO.getBundleName());
                userBundleEnrollmentOutDTO.setTotalCourses(bundleInfoOutDTO.getTotalCourses());
                userBundleEnrollmentOutDTO.setActive(bundleInfoOutDTO.isActive());
                userBundleEnrollmentOutDTO.setAverageCompletion(98F);

                List<UserBundleEnrollment> userBundleEnrollments = userBundleEnrollmentRepository.findByBundleId(bundleInfoOutDTO.getBundleId());
                if (userBundleEnrollments != null && !userBundleEnrollments.isEmpty()) {
                    //throw new ResourceNotFoundException("No bundle enrollments for user");
                    List<EnrolledUserOutDTO> enrolledUserOutDTOS = new ArrayList<>();
                    for (UserBundleEnrollment userBundleEnrollment : userBundleEnrollments) {
                        User user = userRepository.findById(userBundleEnrollment.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                        User assignedByName = userRepository.findById(userBundleEnrollment.getAssignedBy())
                                .orElseThrow(() -> new ResourceNotFoundException("Assigner not found"));
                        EnrolledUserOutDTO enrolledDTO = getEnrolledUserDTO(userBundleEnrollment, user, assignedByName);
                        enrolledUserOutDTOS.add(enrolledDTO);
                    }
                    userBundleEnrollmentOutDTO.setEnrolledUserOutDTOList(enrolledUserOutDTOS);
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

    private static EnrolledUserOutDTO getEnrolledUserDTO(UserBundleEnrollment userBundleEnrollment, User user, User assignedByName) {
        EnrolledUserOutDTO enrolledDTO = new EnrolledUserOutDTO();
        enrolledDTO.setUserId(user.getUserId());
        enrolledDTO.setUserName(user.getFirstName() + " " + user.getLastName());
        enrolledDTO.setEnrollmentDate(userBundleEnrollment.getAssignedAt());
        enrolledDTO.setDeadline(userBundleEnrollment.getDeadline());
        enrolledDTO.setProgress(98.0);
        enrolledDTO.setAssignedByName(assignedByName.getFirstName() + assignedByName.getLastName());
        return enrolledDTO;
    }
}
