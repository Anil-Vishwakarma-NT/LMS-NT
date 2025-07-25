package com.nt.user_service_lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Table declaration for Role.
 */
@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    /**
     * Checks if the object is equal.
     *
     * @param o
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId) && Objects.equals(name, role.name);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(roleId, name);
    }
}
