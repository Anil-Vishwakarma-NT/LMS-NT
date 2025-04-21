package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.EnrollmentConverter;
import com.nt.LMS.dto.CourseBundleDTO;
import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.InvalidRequestException;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.repository.EnrollmentRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public String enroll(EnrollmentDTO enrollmentDTO) {
        try {

            if (!userRepository.existsById(enrollmentDTO.getUserId())) {
                throw new ResourceNotFoundException("User not found");
            }
            if (!courseMicroserviceClient.courseExistsById(enrollmentDTO.getCourseId())) {
                throw new ResourceNotFoundException("Course not found");
            }
            if (!userRepository.existsById(enrollmentDTO.getAssignedBy())) {
                throw new ResourceNotFoundException("Course assigner not found");
            }
            Optional<User> user = userRepository.findById(enrollmentDTO.getUserId());
            if(user.isPresent()) {
                if (!(Objects.equals(user.get().getManagerId(), enrollmentDTO.getAssignedBy()))) {
                    throw new InvalidRequestException("You cannot assign course to this user");
                }
            }
            if ( enrollmentDTO.getBundleId() == null || enrollmentDTO.getBundleId() == 0) {
                if(enrollmentRepository.existsByUserIdAndCourseId(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId())) {
                    throw new ResourceConflictException("User Already Enrolled in this course");
                }
                Enrollment enrollment = EnrollmentConverter.enrollmentDtoToEnrollment(enrollmentDTO);
                enrollmentRepository.save(enrollment);
            }
            else {
                if (!courseMicroserviceClient.bundleExistsById(enrollmentDTO.getBundleId())) {
                    throw new ResourceNotFoundException("Bundle not found");
                }
                List<CourseBundleDTO> courseBundleDTOS = courseMicroserviceClient.getAllCoursesByBundleId(enrollmentDTO.getBundleId()).getBody();
                if (courseBundleDTOS == null || courseBundleDTOS.isEmpty()) {
                    throw new ResourceNotFoundException("No courses found in bundle");
                }
                for (CourseBundleDTO courseBundleDTO : courseBundleDTOS) {
                    Long courseId = courseBundleDTO.getCourseId();
                    boolean existingEnrollment = enrollmentRepository.existsByUserIdAndCourseId(
                            enrollmentDTO.getUserId(), courseId);
                    if(existingEnrollment) {
                        Enrollment enrollment = enrollmentRepository.getByUserIdAndCourseId(enrollmentDTO.getUserId(), courseId);

                    }
                    else {
                        EnrollmentDTO dto = new EnrollmentDTO();
                        dto.setUserId(enrollmentDTO.getUserId());
                        dto.setCourseId(courseId);
                        dto.setBundleId(enrollmentDTO.getBundleId());
                        dto.setAssignedBy(enrollmentDTO.getAssignedBy());
                        dto.setDeadline(enrollmentDTO.getDeadline());
                        dto.setIsIndividualAssigned(enrollmentDTO.getIsIndividualAssigned());
                        dto.setIsGroupAssigned(enrollmentDTO.getIsGroupAssigned());
                        Enrollment enrollment = EnrollmentConverter.enrollmentDtoToEnrollment(dto);
                        enrollmentRepository.save(enrollment);
                    }
                }
            }
            return "Enrollment Successful";
        } catch (ResourceNotFoundException | ResourceConflictException | InvalidRequestException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
    }
    @Override
    public long countEnrollments() {
        return enrollmentRepository.count();
    }
}
