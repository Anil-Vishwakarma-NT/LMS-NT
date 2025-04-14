package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bundle")
@Data
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bundle_id")
    private long bundleId;

    @Column(name = "bundle_name")
    private String bundleName;

    public Bundle(long bundleId, String bundleName) {
        this.bundleId = bundleId;
        this.bundleName = bundleName;
    }
    public Bundle() {
    }
}
