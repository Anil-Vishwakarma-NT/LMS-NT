package com.nt.LMS.dto.outDTO;

import java.math.BigDecimal;

public class EnrollmentStatsDTO {
    private Long totalEnrollments;
    private Long totalUsersEnrolled;
    private Long totalGroupsEnrolled;
    private CourseEnrollmentStatsDTO topEnrolledCourse;
    private Long totalBundlesEnrolled;
    private BigDecimal averageProgressPercentage;

    // Inner class for top enrolled course details
    public static class CourseEnrollmentStatsDTO {
        private Long courseId;
        private Long enrollmentCount;

        public CourseEnrollmentStatsDTO(Long courseId, Long enrollmentCount) {
            this.courseId = courseId;
            this.enrollmentCount = enrollmentCount;
        }

        // Getters and setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        public Long getEnrollmentCount() { return enrollmentCount; }
        public void setEnrollmentCount(Long enrollmentCount) { this.enrollmentCount = enrollmentCount; }
    }

    // Constructors
    public EnrollmentStatsDTO() {}

    public EnrollmentStatsDTO(Long totalEnrollments, Long totalUsersEnrolled,
                              Long totalGroupsEnrolled, CourseEnrollmentStatsDTO topEnrolledCourse,
                              Long totalBundlesEnrolled, BigDecimal averageProgressPercentage) {
        this.totalEnrollments = totalEnrollments;
        this.totalUsersEnrolled = totalUsersEnrolled;
        this.totalGroupsEnrolled = totalGroupsEnrolled;
        this.topEnrolledCourse = topEnrolledCourse;
        this.totalBundlesEnrolled = totalBundlesEnrolled;
        this.averageProgressPercentage = averageProgressPercentage;
    }

    // Getters and setters
    public Long getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(Long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public Long getTotalUsersEnrolled() { return totalUsersEnrolled; }
    public void setTotalUsersEnrolled(Long totalUsersEnrolled) { this.totalUsersEnrolled = totalUsersEnrolled; }

    public Long getTotalGroupsEnrolled() { return totalGroupsEnrolled; }
    public void setTotalGroupsEnrolled(Long totalGroupsEnrolled) { this.totalGroupsEnrolled = totalGroupsEnrolled; }

    public CourseEnrollmentStatsDTO getTopEnrolledCourse() { return topEnrolledCourse; }
    public void setTopEnrolledCourse(CourseEnrollmentStatsDTO topEnrolledCourse) { this.topEnrolledCourse = topEnrolledCourse; }

    public Long getTotalBundlesEnrolled() { return totalBundlesEnrolled; }
    public void setTotalBundlesEnrolled(Long totalBundlesEnrolled) { this.totalBundlesEnrolled = totalBundlesEnrolled; }

    public BigDecimal getAverageProgressPercentage() { return averageProgressPercentage; }
    public void setAverageProgressPercentage(BigDecimal averageProgressPercentage) { this.averageProgressPercentage = averageProgressPercentage; }
}
