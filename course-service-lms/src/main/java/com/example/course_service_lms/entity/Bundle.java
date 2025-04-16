package com.example.course_service_lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
     * Constructs a {@code Bundle} with the given ID and name.
     *
     * @param bundleId    the unique ID of the bundle
     * @param bundleName  the name of the bundle
     */
    public Bundle(final long bundleId, final String bundleName) {
        this.bundleId = bundleId;
        this.bundleName = bundleName;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Bundle() {
    }

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
