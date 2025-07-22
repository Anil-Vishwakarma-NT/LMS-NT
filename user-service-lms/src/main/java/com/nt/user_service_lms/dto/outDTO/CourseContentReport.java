package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO representing a report of course content.
 */
@Data
@Builder
public final class CourseContentReport {
    /**
     * Name of the course content.
     */
    private final String contentName;

    /**
     * Description of the course content.
     */
    private final String contentDescription;

    /**
     * Creation date and time of the course content.
     */
    private final LocalDateTime contentCreatedAt;

    /**
     * Constructs a new CourseContentReport.
     *
     * @param contentName        the name of the content
     * @param contentDescription the description of the content
     * @param contentCreatedAt   the creation date and time of the content
     */
    public CourseContentReport(final String contentName, final String contentDescription, final LocalDateTime contentCreatedAt) {
        this.contentName = contentName;
        this.contentDescription = contentDescription;
        this.contentCreatedAt = contentCreatedAt;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseContentReport)) {
            return false;
        }
        CourseContentReport that = (CourseContentReport) o;
        return Objects.equals(contentName, that.contentName)
                && Objects.equals(contentDescription, that.contentDescription)
                && Objects.equals(contentCreatedAt, that.contentCreatedAt);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(contentName, contentDescription, contentCreatedAt);
    }
}

