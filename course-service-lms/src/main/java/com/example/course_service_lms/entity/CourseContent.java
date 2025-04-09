package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "course_content")
@Data
public class CourseContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_content_id")
    private long courseContentId;

    @Column(name = "course_id")
    private long courseId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "video_link")
    private String videoLink;

    @Column(name = "resource_link")
    private String resourceLink;

    public CourseContent(long courseContentId, long courseId, String title, String description, String videoLink, String resourceLink) {
        this.courseContentId = courseContentId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.videoLink = videoLink;
        this.resourceLink = resourceLink;
    }

    public CourseContent() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseContent that = (CourseContent) o;
        return courseContentId == that.courseContentId && courseId == that.courseId && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(videoLink, that.videoLink) && Objects.equals(resourceLink, that.resourceLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseContentId, courseId, title, description, videoLink, resourceLink);
    }
}
