package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;

    @Column(nullable = false, unique = true)
    private String name;

}
