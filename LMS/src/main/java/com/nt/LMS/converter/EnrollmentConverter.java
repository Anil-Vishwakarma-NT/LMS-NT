package com.nt.LMS.converter;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.Enrollment;

import java.time.LocalDateTime;

public class EnrollmentConverter {
    public static Enrollment enrollmentDtoToEnrollment(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(enrollmentDTO.getUserId());
        enrollment.setCourseId(enrollmentDTO.getCourseId());
        enrollment.setBundleId(enrollmentDTO.getBundleId());
        enrollment.setAssignedBy(enrollmentDTO.getAssignedBy());
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setDeadline(enrollmentDTO.getDeadline());
        enrollment.setEnrolled(true);
        enrollment.setIndividualAssigned(enrollmentDTO.getIsIndividualAssigned());
        enrollment.setGroupAssigned(enrollmentDTO.getIsGroupAssigned());
        return enrollment;
    }
}
