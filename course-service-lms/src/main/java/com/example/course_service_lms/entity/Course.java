package com.example.course_service_lms.entity;

import com.example.course_service_lms.Enum.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
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

}


