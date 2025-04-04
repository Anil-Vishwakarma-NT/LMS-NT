package com.example.course_service_lms.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_bundle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_bundle_id")
    private long courseBundleId;

    @Column(name = "bundle_id")
    private long bundleId;

    @Column(name = "course_id")
    private long courseId;
}


