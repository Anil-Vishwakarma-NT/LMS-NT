package com.example.course_service_lms.entity;

import com.example.course_service_lms.Enum.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="category")
    private String category;

    @Column(name="type")
    private String type;

    @Lob
    @Column(name="image")
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(name="level")
    private CourseLevel level;

}


