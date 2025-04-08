package com.example.course_service_lms.entity;

import com.example.course_service_lms.Enum.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "course")
@Data

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private long courseId;

    @Column(name = "ownerId")
    private long ownerId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name="level")
    private CourseLevel level;

    public Course(long courseId, long ownerId, String title, String description, String image, CourseLevel level) {
        this.courseId = courseId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.image = image;
        this.level = level;
    }

    public Course() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId && ownerId == course.ownerId && Objects.equals(title, course.title) && Objects.equals(description, course.description) && Objects.equals(image, course.image) && level == course.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, ownerId, title, description, image, level);
    }
}


