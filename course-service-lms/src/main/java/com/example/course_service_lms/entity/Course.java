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
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private long courseId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Lob
    @Column(name="image", columnDefinition = "BYTEA")
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(name="level")
    private CourseLevel level;

}


