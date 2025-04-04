package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bundle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bundle_id")
    private long bundleId;

    @Column(name = "bundle_name")
    private String bundleName;
}
