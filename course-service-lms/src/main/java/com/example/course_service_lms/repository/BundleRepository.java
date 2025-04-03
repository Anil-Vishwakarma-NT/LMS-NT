package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository extends JpaRepository <Bundle,Long>{
    boolean existsByBundleName(String bundleName);
}
