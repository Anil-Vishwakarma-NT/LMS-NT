package com.nt.user_service_lms.dto.outDTO;

import java.math.BigDecimal;

/**
 * DTO representing enrollment statistics.
 */
public class EnrollmentStatsDTO {

    /**
     * Total number of enrollments.
     */
    private Long totalEnrollments;

    /**
     * Total number of users enrolled.
     */
    private Long totalUsersEnrolled;

    /**
     * Total number of groups enrolled.
     */
    private Long totalGroupsEnrolled;

    /**
     * Details of the top enrolled course.
     */
    private CourseEnrollmentStatsDTO topEnrolledCourse;

    /**
     * Total number of bundles enrolled.
     */
    private Long totalBundlesEnrolled;

    /**
     * Average progress percentage.
     */
    private BigDecimal averageProgressPercentage;

    /**
     * DTO for top enrolled course details.
     */
    public static class CourseEnrollmentStatsDTO {

        /**
         * ID of the course.
         */
        private Long courseId;

        /**
         * Enrollment count for the course.
         */
        private Long enrollmentCount;

        /**
         * Constructs a new CourseEnrollmentStatsDTO.
         *
         * @param courseId        the course ID
         * @param enrollmentCount the enrollment count
         */
        public CourseEnrollmentStatsDTO(final Long courseId, final Long enrollmentCount) {
            this.courseId = courseId;
            this.enrollmentCount = enrollmentCount;
        }

        /**
         * Gets the course ID.
         *
         * @return the course ID
         */
        public Long getCourseId() {
            return courseId;
        }

        /**
         * Sets the course ID.
         *
         * @param courseId the course ID
         */
        public void setCourseId(final Long courseId) {
            this.courseId = courseId;
        }

        /**
         * Gets the enrollment count.
         *
         * @return the enrollment count
         */
        public Long getEnrollmentCount() {
            return enrollmentCount;
        }

        /**
         * Sets the enrollment count.
         *
         * @param enrollmentCount the enrollment count
         */
        public void setEnrollmentCount(final Long enrollmentCount) {
            this.enrollmentCount = enrollmentCount;
        }
    }

    /**
     * Default constructor.
     */
    public EnrollmentStatsDTO() {
    }

    /**
     * Constructs a new EnrollmentStatsDTO.
     *
     * @param totalEnrollments          total enrollments
     * @param totalUsersEnrolled        total users enrolled
     * @param totalGroupsEnrolled       total groups enrolled
     * @param topEnrolledCourse         top enrolled course details
     * @param totalBundlesEnrolled      total bundles enrolled
     * @param averageProgressPercentage average progress percentage
     */
    public EnrollmentStatsDTO(
            final Long totalEnrollments,
            final Long totalUsersEnrolled,
            final Long totalGroupsEnrolled,
            final CourseEnrollmentStatsDTO topEnrolledCourse,
            final Long totalBundlesEnrolled,
            final BigDecimal averageProgressPercentage) {
        this.totalEnrollments = totalEnrollments;
        this.totalUsersEnrolled = totalUsersEnrolled;
        this.totalGroupsEnrolled = totalGroupsEnrolled;
        this.topEnrolledCourse = topEnrolledCourse;
        this.totalBundlesEnrolled = totalBundlesEnrolled;
        this.averageProgressPercentage = averageProgressPercentage;
    }

    /**
     * Gets the total enrollments.
     *
     * @return the total enrollments
     */
    public Long getTotalEnrollments() {
        return totalEnrollments;
    }

    /**
     * Sets the total enrollments.
     *
     * @param totalEnrollments the total enrollments
     */
    public void setTotalEnrollments(final Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    /**
     * Gets the total users enrolled.
     *
     * @return the total users enrolled
     */
    public Long getTotalUsersEnrolled() {
        return totalUsersEnrolled;
    }

    /**
     * Sets the total users enrolled.
     *
     * @param totalUsersEnrolled the total users enrolled
     */
    public void setTotalUsersEnrolled(final Long totalUsersEnrolled) {
        this.totalUsersEnrolled = totalUsersEnrolled;
    }

    /**
     * Gets the total groups enrolled.
     *
     * @return the total groups enrolled
     */
    public Long getTotalGroupsEnrolled() {
        return totalGroupsEnrolled;
    }

    /**
     * Sets the total groups enrolled.
     *
     * @param totalGroupsEnrolled the total groups enrolled
     */
    public void setTotalGroupsEnrolled(final Long totalGroupsEnrolled) {
        this.totalGroupsEnrolled = totalGroupsEnrolled;
    }

    /**
     * Gets the top enrolled course details.
     *
     * @return the top enrolled course details
     */
    public CourseEnrollmentStatsDTO getTopEnrolledCourse() {
        return topEnrolledCourse;
    }

    /**
     * Sets the top enrolled course details.
     *
     * @param topEnrolledCourse the top enrolled course details
     */
    public void setTopEnrolledCourse(final CourseEnrollmentStatsDTO topEnrolledCourse) {
        this.topEnrolledCourse = topEnrolledCourse;
    }

    /**
     * Gets the total bundles enrolled.
     *
     * @return the total bundles enrolled
     */
    public Long getTotalBundlesEnrolled() {
        return totalBundlesEnrolled;
    }

    /**
     * Sets the total bundles enrolled.
     *
     * @param totalBundlesEnrolled the total bundles enrolled
     */
    public void setTotalBundlesEnrolled(final Long totalBundlesEnrolled) {
        this.totalBundlesEnrolled = totalBundlesEnrolled;
    }

    /**
     * Gets the average progress percentage.
     *
     * @return the average progress percentage
     */
    public BigDecimal getAverageProgressPercentage() {
        return averageProgressPercentage;
    }

    /**
     * Sets the average progress percentage.
     *
     * @param averageProgressPercentage the average progress percentage
     */
    public void setAverageProgressPercentage(final BigDecimal averageProgressPercentage) {
        this.averageProgressPercentage = averageProgressPercentage;
    }
}
