package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String name;

}
