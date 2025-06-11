package com.nt.LMS.converter;

import com.nt.LMS.entities.*;
import com.nt.LMS.feignClient.CourseMicroserviceClient;
import com.nt.LMS.inDTO.EnrollmentInDTO;
import com.nt.LMS.outDTO.EnrolledBundlesOutDTO;
import com.nt.LMS.outDTO.EnrolledCoursesOutDTO;
import com.nt.LMS.outDTO.UserEnrollmentsOutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EnrollmentConvertor {

    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    /**
     * Converts EnrollmentDTO and timestamp to UserCourseEnrollment entity
     */
    public UserCourseEnrollment toUserCourseEnrollment(EnrollmentInDTO enrollmentInDTO, LocalDateTime assignedAt) {
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(enrollmentInDTO.getUserId());
        enrollment.setCourseId(enrollmentInDTO.getCourseId());
        enrollment.setAssignedBy(enrollmentInDTO.getAssignedBy());
        enrollment.setStatus("ENROLLED");
        enrollment.setAssignedAt(assignedAt);
        enrollment.setDeadline(enrollmentInDTO.getDeadline());
        return enrollment;
    }

    /**
     * Converts EnrollmentDTO and timestamp to UserBundleEnrollment entity
     */
    public UserBundleEnrollment toUserBundleEnrollment(EnrollmentInDTO enrollmentInDTO, LocalDateTime assignedAt) {
        UserBundleEnrollment enrollment = new UserBundleEnrollment();
        enrollment.setUserId(enrollmentInDTO.getUserId());
        enrollment.setBundleId(enrollmentInDTO.getBundleId());
        enrollment.setDeadline(enrollmentInDTO.getDeadline());
        enrollment.setStatus("ENROLLED");
        enrollment.setAssignedBy(enrollmentInDTO.getAssignedBy());
        enrollment.setAssignedAt(assignedAt);
        return enrollment;
    }

    /**
     * Converts EnrollmentDTO and timestamp to GroupCourseEnrollment entity
     */
    public GroupCourseEnrollment toGroupCourseEnrollment(EnrollmentInDTO enrollmentInDTO, LocalDateTime assignedAt) {
        GroupCourseEnrollment enrollment = new GroupCourseEnrollment();
        enrollment.setGroupId(enrollmentInDTO.getGroupId());
        enrollment.setCourseId(enrollmentInDTO.getCourseId());
        enrollment.setDeadline(enrollmentInDTO.getDeadline());
        enrollment.setStatus("ENROLLED");
        enrollment.setAssignedBy(enrollmentInDTO.getAssignedBy());
        enrollment.setAssignedAt(assignedAt);
        return enrollment;
    }

    /**
     * Converts EnrollmentDTO and timestamp to GroupBundleEnrollment entity
     */
    public GroupBundleEnrollment toGroupBundleEnrollment(EnrollmentInDTO enrollmentInDTO, LocalDateTime assignedAt) {
        GroupBundleEnrollment enrollment = new GroupBundleEnrollment();
        enrollment.setGroupId(enrollmentInDTO.getGroupId());
        enrollment.setBundleId(enrollmentInDTO.getBundleId());
        enrollment.setDeadline(enrollmentInDTO.getDeadline());
        enrollment.setAssignedBy(enrollmentInDTO.getAssignedBy());
        enrollment.setAssignedAt(assignedAt);
        enrollment.setStatus("ENROLLED");
        return enrollment;
    }

    /**
     * Creates a new Enrollment entity
     */
    public Enrollment toEnrollment(Long userId, Long courseId, Long assignedBy) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setAssignedBy(assignedBy);
        enrollment.setIsEnrolled(true);
        return enrollment;
    }

    /**
     * Creates EnrollmentHistory entity
     */
    public EnrollmentHistory toEnrollmentHistory(Long userId, Long groupId, Long courseId,
                                                 Long bundleId, LocalDateTime deadline,
                                                 Long assignedBy, LocalDateTime recordedAt,
                                                 String actionType) {
        EnrollmentHistory history = new EnrollmentHistory();
        history.setUserId(userId);
        history.setGroupId(groupId);
        history.setCourseId(courseId);
        history.setBundleId(bundleId);
        history.setDeadline(deadline);
        history.setAssignedBy(assignedBy);
        history.setStatus(actionType);
        history.setRecordedAt(recordedAt);
        return history;
    }

    /**
     * Converts UserCourseEnrollment to EnrolledCoursesDTO with course name and progress
     */
    public EnrolledCoursesOutDTO toEnrolledCoursesDTO(UserCourseEnrollment enrollment,
                                                      String courseName,
                                                      Float progress) {
        EnrolledCoursesOutDTO dto = new EnrolledCoursesOutDTO();
        dto.setCourseId(enrollment.getCourseId());
        dto.setCourseName(courseName);
        dto.setEnrollmentDate(enrollment.getAssignedAt());
        dto.setDeadline(enrollment.getDeadline());
        dto.setProgress(progress != null ? progress : 0F);
        return dto;
    }

    /**
     * Converts UserBundleEnrollment to EnrolledBundlesDTO
     */
    public EnrolledBundlesOutDTO toEnrolledBundlesDTO(UserBundleEnrollment enrollment, String bundleName) {
        EnrolledBundlesOutDTO dto = new EnrolledBundlesOutDTO();
        dto.setBundleName(bundleName);
        dto.setBundleId(enrollment.getBundleId());
        dto.setEnrollmentDate(enrollment.getAssignedAt());
        dto.setProgress(50F); // TODO: Calculate actual progress
        dto.setDeadline(enrollment.getDeadline());
        return dto;
    }

    /**
     * Converts User and enrollment data to UserEnrollmentsDTO
     */
    public UserEnrollmentsOutDTO toUserEnrollmentsDTO(User user,
                                                      List<EnrolledCoursesOutDTO> enrolledCourses,
                                                      List<EnrolledBundlesOutDTO> enrolledBundles,
                                                      long courseEnrollments,
                                                      long bundleEnrollments,
                                                      int upcomingDeadlines) {
        UserEnrollmentsOutDTO dto = new UserEnrollmentsOutDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getFirstName() + " " + user.getLastName());
        dto.setStatus(true);
        dto.setCourseEnrollments(courseEnrollments);
        dto.setBundleEnrollments(bundleEnrollments);
        dto.setEnrolledCoursesList(enrolledCourses);
        dto.setEnrolledBundlesList(enrolledBundles);
        dto.setAverageCompletion(50F); // TODO: Calculate actual average
        dto.setUpcomingDeadlines(upcomingDeadlines);
        return dto;
    }

    /**
     * Batch converts UserCourseEnrollments to EnrolledCoursesDTOs
     * More efficient for bulk operations
     */
    public List<EnrolledCoursesOutDTO> toEnrolledCoursesDTOList(List<UserCourseEnrollment> enrollments,
                                                                Map<Long, String> courseNames,
                                                                Map<Long, Float> progressMap) {
        List<EnrolledCoursesOutDTO> dtoList = new ArrayList<>();
        for (UserCourseEnrollment enrollment : enrollments) {
            String courseName = courseNames.getOrDefault(enrollment.getCourseId(), "Unknown Course");
            Float progress = progressMap.getOrDefault(enrollment.getCourseId(), 0F);
            dtoList.add(toEnrolledCoursesDTO(enrollment, courseName, progress));
        }
        return dtoList;
    }

    /**
     * Batch converts UserBundleEnrollments to EnrolledBundlesDTOs
     */
    public List<EnrolledBundlesOutDTO> toEnrolledBundlesDTOList(List<UserBundleEnrollment> enrollments,
                                                                Map<Long, String> bundleNames) {
        List<EnrolledBundlesOutDTO> dtoList = new ArrayList<>();
        for (UserBundleEnrollment enrollment : enrollments) {
            String bundleName = bundleNames.getOrDefault(enrollment.getBundleId(), "Unknown Bundle");
            dtoList.add(toEnrolledBundlesDTO(enrollment, bundleName));
        }
        return dtoList;
    }

    // Utility method to safely get course name
    public String getCourseNameSafely(Long courseId) {
        try {
            ResponseEntity<String> response = courseMicroserviceClient.getCourseNameById(courseId);
            return (response != null && response.getBody() != null) ? response.getBody() : "Unknown Course";
        } catch (Exception e) {
            return "Unknown Course";
        }
    }

    // Utility method to safely get bundle name
    public String getBundleNameSafely(Long bundleId) {
        try {
            ResponseEntity<String> response = courseMicroserviceClient.getBundleNameById(bundleId);
            return (response != null && response.getBody() != null) ? response.getBody() : "Unknown Bundle";
        } catch (Exception e) {
            return "Unknown Bundle";
        }
    }

    // Utility method to safely get progress
    public Float getProgressSafely(int userId, int courseId) {
        try {
            ResponseEntity<Double> response = courseMicroserviceClient.getCourseProgress(userId, courseId);
            return response != null && response.getBody() != null ? response.getBody().floatValue() : 0F;
        } catch (Exception e) {
            return 0F;
        }
    }
}
