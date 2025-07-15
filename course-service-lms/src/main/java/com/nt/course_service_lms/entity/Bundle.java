package com.nt.course_service_lms.entity;

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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a course bundle in the Learning Management System (LMS).
 *
 * <p>A bundle is a logical group of one or more courses. It can be used to organize
 * or sell multiple related courses as a single package.</p>
 *
 * <p>This entity is mapped to the {@code bundle} table in the database.</p>
 */
@Entity
@Table(name = "bundle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bundle {

    /**
     * Unique identifier for the bundle.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bundle_id")
    private long bundleId;

    /**
     * Name of the bundle.
     * <p>Used to label or describe the bundle.</p>
     */
    @Column(name = "bundle_name")
    private String bundleName;

    /**
     * Bundle Is Active or not.
     * <p>Shows if a bundle is active or not.</p>
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Bundle created at this time.
     * <p>Timestamp when the bundle is created</p>
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Bundle updated at this time.
     * <p>Used to label or describe the bundle.</p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Compares this bundle to another object for equality.
     * <p>Two bundles are considered equal if their {@code bundleId} and {@code bundleName} match.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the bundles are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bundle bundle = (Bundle) o;
        return bundleId == bundle.bundleId
                && Objects.equals(bundleName, bundle.bundleName);
    }

    /**
     * Returns a hash code based on {@code bundleId} and {@code bundleName}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(bundleId, bundleName);
    }
}
