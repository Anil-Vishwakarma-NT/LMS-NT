package com.example.course_service_lms.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "course_bundle")
@Data
public class CourseBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_bundle_id")
    private long courseBundleId;

    @Column(name = "bundle_id")
    private long bundleId;

    @Column(name = "course_id")
    private long courseId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseBundle that = (CourseBundle) o;
        return courseBundleId == that.courseBundleId && bundleId == that.bundleId && courseId == that.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, courseId);
    }

    public CourseBundle() {
    }

    public CourseBundle(long courseBundleId, long bundleId, long courseId) {
        this.courseBundleId = courseBundleId;
        this.bundleId = bundleId;
        this.courseId = courseId;
    }

}


