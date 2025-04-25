package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Enrollment getByUserIdAndCourseId(Long userId, Long courseId);
    @Query(value = """
    SELECT course_id
    FROM enrollment
    WHERE is_enrolled = true
    GROUP BY course_id
    ORDER BY COUNT(*) DESC
    LIMIT 1
    """, nativeQuery = true)
    Long findMostFrequentEnrolledCourseId();

}
