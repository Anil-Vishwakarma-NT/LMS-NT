package com.nt.LMS.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Data;

/**
 * Table declaration for Role.
 */
@Entity
@Table(name = "role")
@Data
public class Role {

    /**
     * unique Id for role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    /**
     * Role name.
     */
    @Column(nullable = false, unique = true)
    private String name;

}
