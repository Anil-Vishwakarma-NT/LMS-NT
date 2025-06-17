package com.nt.LMS.dto.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
public class CourseContentReport {
    private String contentName;
    private String contentDescription;
    private LocalDateTime contentCreatedAt;

    public CourseContentReport(String contentName, String contentDescription, LocalDateTime contentCreatedAt) {
        this.contentName = contentName;
        this.contentDescription = contentDescription;
        this.contentCreatedAt = contentCreatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseContentReport)) return false;
        CourseContentReport that = (CourseContentReport) o;
        return Objects.equals(contentName, that.contentName) &&
                Objects.equals(contentDescription, that.contentDescription) &&
                Objects.equals(contentCreatedAt, that.contentCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentName, contentDescription, contentCreatedAt);
    }
}

